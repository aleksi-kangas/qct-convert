module;

#include <array>
#include <cstdint>
#include <fstream>

export module qct:image.tile;

import :palette;
import :util.reader;

namespace qct::image {
/**
 * Represents a 64 x 64 tile of the image. Tiles may be encoded with different algorithms for efficiency purposes.
 */
struct ImageTile final {
  static constexpr std::int32_t HEIGHT = 64;
  static constexpr std::int32_t WIDTH = 64;
  static constexpr std::int32_t PIXEL_COUNT = HEIGHT * WIDTH;
  static constexpr std::int32_t BYTE_COUNT = PIXEL_COUNT * palette::COLOR_CHANNELS;
  static constexpr std::int32_t ROW_BYTE_COUNT = WIDTH * palette::COLOR_CHANNELS;

  using row_bytes_t = std::array<std::uint8_t, ROW_BYTE_COUNT>;
  using bytes_2d_t = std::array<row_bytes_t, HEIGHT>;

  enum class Encoding {
    HUFFMAN_CODING,
    PIXEL_PACKING,
    RUN_LENGTH_ENCODING
  };

  std::int32_t y{};
  std::int32_t x{};
  Encoding encoding{};
  bytes_2d_t bytes_2d{};

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
  return Encoding::RUN_LENGTH_ENCODING;
}

}  // namespace qct::image
