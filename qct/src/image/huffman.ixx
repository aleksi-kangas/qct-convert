module;

#include <cstdint>
#include <fstream>

export module qct:image.huffman;

import :image;
import :palette;
import :palette.sub;
import :util.reader;

/**
 * Decode a huffman-encoded image tile.
 * @param file to read from
 * @param image_tile_byte_offset byte offset of the image tile
 * @param palette the palette
 * @return decoded image tile bytes
 */
namespace qct::image::huffman {
export tile_bytes_2D_t decode(std::ifstream& file, std::int32_t image_tile_byte_offset,
                              const palette::Palette& palette);
}  // namespace qct::image::huffman

namespace qct::image::huffman {
tile_bytes_2D_t decode(std::ifstream& file, const std::int32_t image_tile_byte_offset,
                       const palette::Palette& palette) {
  // TODO
  return {};
}
}  // namespace qct::image::huffman
