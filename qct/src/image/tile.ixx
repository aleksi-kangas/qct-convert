module;

#include <array>
#include <cstdint>
#include <fstream>
#include <iostream>
#include <ranges>
#include <span>

export module qct:image.tile;

import :common.exception;
import :image;
import :util.reader;

export namespace qct::image {
/**
 * Represents a 64 x 64 tile of the image. Tiles of the image may be encoded with different algorithms for efficiency purposes.
 */
struct ImageTile final {
  enum class Encoding {
    HUFFMAN_CODING,
    PIXEL_PACKING,
    RUN_LENGTH_CODING
  };

  std::int32_t y{};
  std::int32_t x{};
  Encoding encoding{};
  tile_bytes_view_t bytes{};

  ImageTile(const std::int32_t y, const std::int32_t x, const Encoding encoding, const tile_bytes_view_t bytes)
      : y{y}, x{x}, encoding{encoding}, bytes{bytes} {}

  /**
   * Determine the encoding of an image tile at the given byte offset.
   * @param[in] file to read from
   * @param[in] image_tile_byte_offset image tile byte offset
   * @return encoding of the image tile
   */
  static Encoding encodingOf(std::ifstream& file, std::int32_t image_tile_byte_offset);
};
}  // namespace qct::image

namespace qct::image {
ImageTile::Encoding ImageTile::encodingOf(std::ifstream& file, const std::int32_t image_tile_byte_offset) {
  const std::uint8_t first_byte = util::readByte(file, image_tile_byte_offset);
  if (first_byte == 0 || first_byte == 255)
    return Encoding::HUFFMAN_CODING;
  if (first_byte > 127)
    return Encoding::PIXEL_PACKING;
  return Encoding::RUN_LENGTH_CODING;
}

}  // namespace qct::image
