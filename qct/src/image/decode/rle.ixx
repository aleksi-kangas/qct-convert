module;

#include <cstdint>
#include <fstream>

export module qct:image.decode.rle;

import :image.decoder;
import :image.decode.palette;
import :image.tile;
import :palette;
import :palette.color;
import :util.reader;

namespace qct::image::decode {
/**
 * A single decoded RLE byte.
 */
struct DecodedRleByte {
  std::int32_t palette_index{0};
  std::int32_t run_length{0};
};

/**
 * A decoder for image tiles using Run Length Encoding (RLE).
 */
export class RLEImageTileDecoder final : public AbstractImageTileDecoder<RLEImageTileDecoder> {
 public:
  explicit RLEImageTileDecoder(const palette::Palette& palette) : AbstractImageTileDecoder{palette} {}
  ~RLEImageTileDecoder() override = default;

  /**
   * Decode bytes of an image tile using Run Length Encoding (RLE).
   * @param file to read from
   * @param image_tile_byte_offset the byte offset of the tile in the image file
   * @return the tile bytes
   */
  [[nodiscard]] ImageTile::bytes_2d_t decodeTileBytes(std::ifstream& file, std::int32_t image_tile_byte_offset) const;

 private:
  /**
   * Decode a single RLE byte.
   * @param[in] rle_byte the RLE byte to decode
   * @param[in] sub_palette the sub-palette
   * @return decoded RLE byte
   */
  static DecodedRleByte decodeRleByte(std::uint8_t rle_byte, const SubPalette& sub_palette);
};

ImageTile::bytes_2d_t RLEImageTileDecoder::decodeTileBytes(std::ifstream& file,
                                                     const std::int32_t image_tile_byte_offset) const {
  const auto sub_palette = SubPalette::parse(file, image_tile_byte_offset, SubPalette::SizeType::NORMAL);
  const std::int32_t pixel_data_byte_offset = image_tile_byte_offset + 0x01 + sub_palette.size;
  ImageTile::bytes_2d_t tile{};
  // In order to avoid reading one byte at a time from the file,
  // read bytes into a buffer assuming the worst case of one byte per pixel,
  // which practically should never occur (64 x 64 = 4096 bytes).
  const std::vector<std::uint8_t> bytes = util::readBytesSafe(file, pixel_data_byte_offset, ImageTile::PIXEL_COUNT);
  std::size_t byte_index{0};
  std::int32_t pixel_count = 0;
  while (pixel_count < ImageTile::PIXEL_COUNT) {
    const std::uint8_t rle_byte = bytes[byte_index++];
    const auto [palette_index, run_length] = decodeRleByte(rle_byte, sub_palette);
    const auto [red, green, blue] = palette.colors[palette_index];
    for (std::int32_t i = 0; i < run_length; ++i) {
      const std::int32_t y = (pixel_count + i) * palette::COLOR_CHANNELS / ImageTile::ROW_BYTE_COUNT;
      const std::int32_t x = (pixel_count + i) * palette::COLOR_CHANNELS % ImageTile::ROW_BYTE_COUNT;
      tile[y][x + 0] = red;
      tile[y][x + 1] = green;
      tile[y][x + 2] = blue;
    }
    pixel_count += run_length;
  }
  return tile;
}

DecodedRleByte RLEImageTileDecoder::decodeRleByte(const std::uint8_t rle_byte, const SubPalette& sub_palette) {
  const std::int32_t sub_palette_index_mask = (1 << sub_palette.bitsRequiredToIndex()) - 1;
  const std::int32_t sub_palette_index = rle_byte & sub_palette_index_mask;
  const std::int32_t run_length = rle_byte >> sub_palette.bitsRequiredToIndex();
  return {.palette_index = sub_palette.palette_indices[sub_palette_index], .run_length = run_length};
}

}  // namespace qct::image::decode
