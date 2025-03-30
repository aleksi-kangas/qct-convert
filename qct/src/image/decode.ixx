module;

#include <algorithm>
#include <array>
#include <cstdint>
#include <fstream>

export module qct:image.decode;

import :image;
import :palette;
import :palette.sub;
import :util.reader;

namespace qct::image::rle {
/**
 * Decode a run-length-encoded image tile.
 * @param[in] file to read from
 * @param[in] image_tile_byte_offset byte offset of the image tile
 * @param[in] palette the palette
 * @return decoded image tile bytes
 */
export tile_bytes_2D_t decode(std::ifstream& file, std::int32_t image_tile_byte_offset,
                              const palette::Palette& palette);

}  // namespace qct::image::rle

namespace qct::image {
/**
 * Deinterlace the rows of the given tile with the bit-reverse sequence.
 * @param[in/out] tile of which rows to deinterlace
 */
void deinterlace_rows(tile_bytes_2D_t& tile);
}  // namespace qct::image

namespace qct::image::rle {

struct DecodedRleByte {
  std::int32_t palette_index{0};
  std::int32_t run_length{0};
};
DecodedRleByte decodeRleByte(const std::uint8_t rle_byte, const palette::SubPalette& sub_palette) {
  const std::int32_t sub_palette_index_mask = (1 << sub_palette.bitsRequiredToIndex()) - 1;
  const std::int32_t sub_palette_index = rle_byte & sub_palette_index_mask;
  const std::int32_t run_length = rle_byte >> sub_palette.bitsRequiredToIndex();
  return {.palette_index = sub_palette.palette_indices[sub_palette_index], .run_length = run_length};
}

tile_bytes_2D_t decode(std::ifstream& file, const std::int32_t image_tile_byte_offset,
                       const palette::Palette& palette) {
  const auto sub_palette =
      palette::SubPalette::parse(file, image_tile_byte_offset, palette::SubPalette::SizeType::NORMAL);
  const std::int32_t pixel_data_byte_offset = image_tile_byte_offset + 0x01 + sub_palette.size;
  std::array<std::array<std::uint8_t, TILE_ROW_BYTE_COUNT>, TILE_HEIGHT> tile{};
  std::int32_t current_tile_byte_offset = pixel_data_byte_offset;
  std::int32_t pixel_count = 0;
  while (pixel_count < TILE_PIXEL_COUNT) {
    const std::uint8_t rle_byte = util::readByte(file, current_tile_byte_offset++);
    const auto [palette_index, run_length] = decodeRleByte(rle_byte, sub_palette);
    const auto [red, green, blue] = palette.colors[palette_index];
    for (std::int32_t i = 0; i < run_length; ++i) {
      const std::int32_t y = (pixel_count + i) * 3 / TILE_ROW_BYTE_COUNT;
      const std::int32_t x = (pixel_count + i) * 3 % TILE_ROW_BYTE_COUNT;
      tile[y][x + 0] = red;
      tile[y][x + 1] = green;
      tile[y][x + 2] = blue;
    }
    pixel_count += run_length;
  }
  deinterlace_rows(tile);
  return tile;
}

}  // namespace qct::image::rle

namespace qct::image {
void deinterlace_rows(tile_bytes_2D_t& tile) {
  std::array<std::int32_t, TILE_HEIGHT> deinterlaced_row_sequence{};
  for (std::int32_t i = 0; i < TILE_HEIGHT; ++i) {
    constexpr std::array interlaced_row_sequence = {0, 32, 16, 48, 8,  40, 24, 56, 4, 36, 20, 52, 12, 44, 28, 60,
                                                    2, 34, 18, 50, 10, 42, 26, 58, 6, 38, 22, 54, 14, 46, 30, 62,
                                                    1, 33, 17, 49, 9,  41, 25, 57, 5, 37, 21, 53, 13, 45, 29, 61,
                                                    3, 35, 19, 51, 11, 43, 27, 59, 7, 39, 23, 55, 15, 47, 31, 63};

    deinterlaced_row_sequence[interlaced_row_sequence[i]] = i;
  }
  const tile_bytes_2D_t temp_tile = tile;
  for (std::int32_t i = 0; i < TILE_HEIGHT; ++i) {
    tile[deinterlaced_row_sequence[i]] = temp_tile[i];
  }
}
}  // namespace qct::image
