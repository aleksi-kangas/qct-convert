module;

#include <algorithm>
#include <cstdint>
#include <filesystem>
#include <fstream>
#include <future>
#include <iostream>
#include <ranges>
#include <span>
#include <utility>
#include <variant>
#include <vector>

export module qct:image.index;

import :image.decode;
import :image.tile;
import :meta;
import :palette;
import :util.reader;

export namespace qct::image {
/**
 * +--------+-------------------+--------------------------------------------+
 * | Offset | Size (Bytes)      | Content                                    |
 * +--------+-------------------+--------------------------------------------+
 * | 0x45A0 | w x h x 4         | Image Index Pointers - QC3 files omit this |
 * +--------+-------------------+--------------------------------------------+
 */
struct ImageIndex final {
  static constexpr std::int32_t BYTE_OFFSET{0x45A0};

  std::vector<std::uint8_t> image_bytes{};

  [[nodiscard]] auto imageBytesView() const;
  [[nodiscard]] std::vector<std::uint8_t> channelBytes(std::int32_t channel_index) const;

  static ImageIndex parse(const std::filesystem::path& filepath, const meta::Metadata& metadata,
                          const palette::Palette& palette);

 private:
  struct ImageTileParseTask final {
    const std::filesystem::path& filepath;
    const meta::Metadata& metadata;
    const palette::Palette& palette;
    const std::int32_t y_tile;
    const std::int32_t x_tile;
  };

  /**
   * Parse an image tile from the file and store it in the image bytes.
   * @param task the image tile parse task
   * @param image_bytes the image bytes to store the parsed tile in
   */
  static std::future<void> parseImageTileAsync(ImageTileParseTask&& task, std::vector<std::uint8_t>& image_bytes);

  /**
   * Read the pointer (byte offset) to an image tile from the image index.
   * @param file the file to read from
   * @param width_tiles width of the image in tiles
   * @param y_tile the y index of the tile to read
   * @param x_tile the x index of the tile to read
   * @return the pointer (byte offset) of the image tile
   */
  static std::int32_t readImageTilePointer(std::ifstream& file, std::int32_t width_tiles, std::int32_t y_tile,
                                           std::int32_t x_tile);

  /**
   * Copy the bytes of an image tile into the image bytes.
   * @param y_tile the y index of the tile to copy
   * @param x_tile the x index of the tile to copy
   * @param tile_bytes the bytes of the tile to copy
   * @param image_width_bytes the width of the image in bytes
   * @param image_bytes the bytes of the image to copy into
   */
  static void copyTileToImage(std::int32_t y_tile, std::int32_t x_tile, const ImageTile::bytes_2d_t& tile_bytes,
                              std::int32_t image_width_bytes, std::vector<std::uint8_t>& image_bytes);
};

auto ImageIndex::imageBytesView() const {
  return std::span(image_bytes.data(), image_bytes.size());
}

std::vector<std::uint8_t> ImageIndex::channelBytes(const std::int32_t channel_index) const {
  if (channel_index < 0 || palette::COLOR_CHANNELS <= channel_index)
    throw std::invalid_argument{"Invalid channel index"};
  const auto channel_view = std::views::iota(0u, image_bytes.size() / 3) |
                            std::views::transform([this, channel_index](auto idx) -> const std::uint8_t& {
                              return image_bytes[idx * 3 + channel_index];
                            });
  // TODO: Replace with std::ranges::to
  std::vector<std::uint8_t> result;
  for (const auto& value : channel_view) {
    result.push_back(value);
  }
  return result;
}

ImageIndex ImageIndex::parse(const std::filesystem::path& filepath, const meta::Metadata& metadata,
                             const palette::Palette& palette) {
  std::vector<std::uint8_t> image_bytes(metadata.height_tiles * metadata.width_tiles * ImageTile::BYTE_COUNT);
  std::vector<std::future<void>> image_tile_futures;
  image_tile_futures.reserve(metadata.height_tiles * metadata.width_tiles);
  for (std::int32_t y_tile = 0; y_tile < metadata.height_tiles; ++y_tile) {
    for (std::int32_t x_tile = 0; x_tile < metadata.width_tiles; ++x_tile) {
      image_tile_futures.emplace_back(parseImageTileAsync(
          {.filepath = filepath, .metadata = metadata, .palette = palette, .y_tile = y_tile, .x_tile = x_tile},
          image_bytes));
    }
  }
  std::ranges::for_each(image_tile_futures, [](auto& future) { future.wait(); });
  return {.image_bytes = std::move(image_bytes)};
}

std::future<void> ImageIndex::parseImageTileAsync(ImageTileParseTask&& task, std::vector<std::uint8_t>& image_bytes) {
  return std::async(
      std::launch::async,
      [&image_bytes](const ImageTileParseTask& t) {
        std::ifstream file{t.filepath, std::ios::binary};
        const std::int32_t image_tile_byte_offset =
            readImageTilePointer(file, t.metadata.width_tiles, t.y_tile, t.x_tile);
        const ImageTile::Encoding tile_encoding = ImageTile::encodingOf(file, image_tile_byte_offset);
        const auto image_tile_decoder = decode::makeImageTileDecoder(tile_encoding, t.palette);
        std::visit(decode::Overloaded{[&](auto& decoder) {
                     const ImageTile::bytes_2d_t tile_bytes_2d = decoder.decodeTile(file, image_tile_byte_offset);
                     copyTileToImage(t.y_tile, t.x_tile, tile_bytes_2d,
                                     t.metadata.width_tiles * ImageTile::ROW_BYTE_COUNT, image_bytes);
                   }},
                   image_tile_decoder);
      },
      task);
}

std::int32_t ImageIndex::readImageTilePointer(std::ifstream& file, const std::int32_t width_tiles,
                                              const std::int32_t y_tile, const std::int32_t x_tile) {
  const std::int32_t image_tile_pointer_offset = (width_tiles * y_tile + x_tile) * 0x04;
  return util::readInt(file, BYTE_OFFSET + image_tile_pointer_offset);
}

void ImageIndex::copyTileToImage(const std::int32_t y_tile, const std::int32_t x_tile,
                                 const ImageTile::bytes_2d_t& tile_bytes, const std::int32_t image_width_bytes,
                                 std::vector<std::uint8_t>& image_bytes) {
  const std::int32_t tile_image_byte_offset =
      y_tile * ImageTile::HEIGHT * image_width_bytes + x_tile * ImageTile::ROW_BYTE_COUNT;
  for (std::int32_t tile_row = 0; tile_row < ImageTile::HEIGHT; ++tile_row) {
    const ImageTile::row_bytes_t& tile_row_bytes = tile_bytes[tile_row];
    const std::int32_t row_offset = tile_image_byte_offset + tile_row * image_width_bytes;
    std::ranges::copy(tile_row_bytes, image_bytes.begin() + row_offset);
  }
}

}  // namespace qct::image
