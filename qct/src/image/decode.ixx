module;

#include <algorithm>
#include <array>
#include <cstdint>
#include <fstream>

export module qct:image.decode;

import :image;
import :image.huffman;
import :image.pp;
import :image.rle;
import :image.tile;
import :palette;
import :palette.sub;
import :util.reader;

namespace qct::image {
/**
 * Decode the given image tile encoded with the given encoding.
 * @param file to read from
 * @param image_tile_byte_offset byte offset of the image tile
 * @param palette the palette
 * @param encoding encoding of the image tile
 * @return
 */
export tile_bytes_2D_t decode(std::ifstream& file, std::int32_t image_tile_byte_offset, const palette::Palette& palette,
                              ImageTile::Encoding encoding);

/**
 * Deinterlace the rows of the given tile with the bit-reverse sequence.
 * @param[in/out] tile_bytes of which rows to deinterlace
 */
void deinterlace_rows(tile_bytes_2D_t& tile_bytes);
}  // namespace qct::image

namespace qct::image {
tile_bytes_2D_t decode(std::ifstream& file, const std::int32_t image_tile_byte_offset, const palette::Palette& palette,
                       const ImageTile::Encoding encoding) {
  tile_bytes_2D_t tile_bytes;
  switch (encoding) {
    case ImageTile::Encoding::HUFFMAN_CODING: {
      tile_bytes = huffman::decode(file, image_tile_byte_offset, palette);
    } break;
    case ImageTile::Encoding::PIXEL_PACKING: {
      tile_bytes = pp::decode(file, image_tile_byte_offset, palette);
    } break;
    case ImageTile::Encoding::RUN_LENGTH_CODING: {
      tile_bytes = rle::decode(file, image_tile_byte_offset, palette);
    } break;
    default: {
      tile_bytes = {};
    }
  }
  deinterlace_rows(tile_bytes);
  return tile_bytes;
}

void deinterlace_rows(tile_bytes_2D_t& tile_bytes) {
  std::array<std::int32_t, TILE_HEIGHT> deinterlaced_row_sequence{};
  for (std::int32_t i = 0; i < TILE_HEIGHT; ++i) {
    constexpr std::array interlaced_row_sequence = {0, 32, 16, 48, 8,  40, 24, 56, 4, 36, 20, 52, 12, 44, 28, 60,
                                                    2, 34, 18, 50, 10, 42, 26, 58, 6, 38, 22, 54, 14, 46, 30, 62,
                                                    1, 33, 17, 49, 9,  41, 25, 57, 5, 37, 21, 53, 13, 45, 29, 61,
                                                    3, 35, 19, 51, 11, 43, 27, 59, 7, 39, 23, 55, 15, 47, 31, 63};

    deinterlaced_row_sequence[interlaced_row_sequence[i]] = i;
  }
  const tile_bytes_2D_t temp_tile_bytes = tile_bytes;
  for (std::int32_t i = 0; i < TILE_HEIGHT; ++i) {
    tile_bytes[deinterlaced_row_sequence[i]] = temp_tile_bytes[i];
  }
}
}  // namespace qct::image
