module;

#include <algorithm>
#include <cstdint>
#include <fstream>
#include <iostream>
#include <utility>
#include <vector>

export module qct:image.index;

import :image;
import :image.decode;
import :image.tile;
import :palette;
import :util.reader;

export namespace qct::image {
/**
 *
 */
struct ImageIndex final {
  image_bytes_t bytes{};

  static ImageIndex parse(std::ifstream& file, std::int32_t byte_offset, std::int32_t height_tiles,
                          std::int32_t width_tiles, const palette::Palette& palette);

 private:
  static std::int32_t readImageTilePointer(std::ifstream& file, std::int32_t image_index_byte_offset,
                                           std::int32_t width_tiles, std::int32_t y_tile, std::int32_t x_tile);

  static void copyTileToImage(std::int32_t y_tile, std::int32_t x_tile, const tile_bytes_2D_t& tile_bytes,
                              std::int32_t image_width_bytes, std::vector<std::uint8_t>& bytes);
};
}  // namespace qct::image

namespace qct::image {
ImageIndex ImageIndex::parse(std::ifstream& file, const std::int32_t byte_offset, const std::int32_t height_tiles,
                             const std::int32_t width_tiles, const palette::Palette& palette) {
  image_bytes_t bytes(height_tiles * width_tiles * TILE_BYTE_COUNT);
  for (std::int32_t y_tile = 0; y_tile < height_tiles; ++y_tile) {
    for (std::int32_t x_tile = 0; x_tile < width_tiles; ++x_tile) {
      const std::int32_t image_tile_byte_offset = readImageTilePointer(file, byte_offset, width_tiles, y_tile, x_tile);
      const ImageTile::Encoding tile_encoding = ImageTile::encodingOf(file, image_tile_byte_offset);
      tile_bytes_2D_t tile_bytes{};
      switch (tile_encoding) {
        case ImageTile::Encoding::RUN_LENGTH_CODING: {
          tile_bytes = rle::decode(file, image_tile_byte_offset, palette);
        } break;
        default:
          break;
      }
      copyTileToImage(y_tile, x_tile, tile_bytes, width_tiles * TILE_ROW_BYTE_COUNT, bytes);
    }
  }
  return {.bytes = std::move(bytes)};
}

std::int32_t ImageIndex::readImageTilePointer(std::ifstream& file, const std::int32_t image_index_byte_offset,
                                              const std::int32_t width_tiles, const std::int32_t y_tile,
                                              const std::int32_t x_tile) {
  const std::int32_t image_tile_pointer_offset = (width_tiles * y_tile + x_tile) * 0x04;
  return util::readInt(file, image_index_byte_offset + image_tile_pointer_offset);
}

void ImageIndex::copyTileToImage(const std::int32_t y_tile, const std::int32_t x_tile,
                                 const tile_bytes_2D_t& tile_bytes, const std::int32_t image_width_bytes,
                                 std::vector<std::uint8_t>& bytes) {
  const std::int32_t tile_first_byte_offset = y_tile * TILE_HEIGHT * image_width_bytes + x_tile * TILE_ROW_BYTE_COUNT;
  for (std::int32_t tile_row = 0; tile_row < TILE_HEIGHT; ++tile_row) {
    const std::array<std::uint8_t, TILE_ROW_BYTE_COUNT>& tile_row_bytes = tile_bytes[tile_row];
    const std::int32_t row_offset = tile_first_byte_offset + tile_row * image_width_bytes;
    std::ranges::copy(tile_row_bytes, bytes.begin() + row_offset);
  }
}
}  // namespace qct::image
