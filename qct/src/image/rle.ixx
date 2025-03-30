module;

#include <cstdint>
#include <fstream>

export module qct:image.rle;

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

/**
 * A single decoded RLE byte.
 */
struct DecodedRleByte {
  std::int32_t palette_index{0};
  std::int32_t run_length{0};
};

/**
 * Decode a single RLE byte.
 * @param[in] rle_byte the RLE byte to decode
 * @param[in] sub_palette the sub-palette
 * @return decoded RLE byte
 */
DecodedRleByte decodeRleByte(std::uint8_t rle_byte, const palette::SubPalette& sub_palette);
}  // namespace qct::image::rle

namespace qct::image::rle {

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
  tile_bytes_2D_t tile{};
  std::int32_t current_tile_byte_offset = pixel_data_byte_offset;
  std::int32_t pixel_count = 0;
  while (pixel_count < TILE_PIXEL_COUNT) {
    const std::uint8_t rle_byte = util::readByte(file, current_tile_byte_offset++);
    const auto [palette_index, run_length] = decodeRleByte(rle_byte, sub_palette);
    const auto [red, green, blue] = palette.colors[palette_index];
    for (std::int32_t i = 0; i < run_length; ++i) {
      const std::int32_t y = (pixel_count + i) * palette::COLOR_CHANNELS / TILE_ROW_BYTE_COUNT;
      const std::int32_t x = (pixel_count + i) * palette::COLOR_CHANNELS % TILE_ROW_BYTE_COUNT;
      tile[y][x + 0] = red;
      tile[y][x + 1] = green;
      tile[y][x + 2] = blue;
    }
    pixel_count += run_length;
  }
  return tile;
}

}  // namespace qct::image::rle
