module;

#include <algorithm>
#include <cstdint>
#include <filesystem>
#include <fstream>
#include <future>
#include <iostream>
#include <utility>
#include <vector>

export module qct:image.index;

import :image;
import :image.decode;
import :image.tile;
import :meta;
import :palette;
import :util.reader;

export namespace qct::image {
/**
 *
 */
struct ImageIndex final {
  static constexpr std::int32_t BYTE_OFFSET{0x45A0};

  image_bytes_t image_bytes{};

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

  static std::future<void> parseImageTileAsync(ImageTileParseTask&& task, image_bytes_t& image_bytes);

  static std::int32_t readImageTilePointer(std::ifstream& file, std::int32_t image_index_byte_offset,
                                           std::int32_t width_tiles, std::int32_t y_tile, std::int32_t x_tile);

  static void copyTileToImage(std::int32_t y_tile, std::int32_t x_tile, const tile_bytes_2D_t& tile_bytes,
                              std::int32_t image_width_bytes, image_bytes_t& bytes);
};
}  // namespace qct::image

namespace qct::image {
ImageIndex ImageIndex::parse(const std::filesystem::path& filepath, const meta::Metadata& metadata,
                             const palette::Palette& palette) {
  image_bytes_t image_bytes(metadata.height_tiles * metadata.width_tiles * TILE_BYTE_COUNT);
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

std::future<void> ImageIndex::parseImageTileAsync(ImageTileParseTask&& task, image_bytes_t& image_bytes) {
  return std::async(
      std::launch::async,
      [&image_bytes](const ImageTileParseTask& t) {
        std::ifstream file{t.filepath, std::ios::binary};
        const std::int32_t image_tile_byte_offset =
            readImageTilePointer(file, BYTE_OFFSET, t.metadata.width_tiles, t.y_tile, t.x_tile);
        const ImageTile::Encoding tile_encoding = ImageTile::encodingOf(file, image_tile_byte_offset);
        const tile_bytes_2D_t tile_bytes = decode(file, image_tile_byte_offset, t.palette, tile_encoding);
        copyTileToImage(t.y_tile, t.x_tile, tile_bytes, t.metadata.width_tiles * TILE_ROW_BYTE_COUNT, image_bytes);
      },
      task);
}

std::int32_t ImageIndex::readImageTilePointer(std::ifstream& file, const std::int32_t image_index_byte_offset,
                                              const std::int32_t width_tiles, const std::int32_t y_tile,
                                              const std::int32_t x_tile) {
  const std::int32_t image_tile_pointer_offset = (width_tiles * y_tile + x_tile) * 0x04;
  return util::readInt(file, image_index_byte_offset + image_tile_pointer_offset);
}

void ImageIndex::copyTileToImage(const std::int32_t y_tile, const std::int32_t x_tile,
                                 const tile_bytes_2D_t& tile_bytes, const std::int32_t image_width_bytes,
                                 image_bytes_t& bytes) {
  const std::int32_t tile_first_byte_offset = y_tile * TILE_HEIGHT * image_width_bytes + x_tile * TILE_ROW_BYTE_COUNT;
  for (std::int32_t tile_row = 0; tile_row < TILE_HEIGHT; ++tile_row) {
    const std::array<std::uint8_t, TILE_ROW_BYTE_COUNT>& tile_row_bytes = tile_bytes[tile_row];
    const std::int32_t row_offset = tile_first_byte_offset + tile_row * image_width_bytes;
    std::ranges::copy(tile_row_bytes, bytes.begin() + row_offset);
  }
}
}  // namespace qct::image
