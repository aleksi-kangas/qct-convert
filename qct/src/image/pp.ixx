module;

#include <cstdint>
#include <fstream>

export module qct:image.pp;

import :image;
import :palette;
import :palette.sub;
import :util.reader;

/**
 * Decode a pixel-packing-encoded image tile.
 * @param file to read from
 * @param image_tile_byte_offset byte offset of the image tile
 * @param palette the palette
 * @return decoded image tile bytes
 */
namespace qct::image::pp {
export tile_bytes_2D_t decode(std::ifstream& file, std::int32_t image_tile_byte_offset,
                              const palette::Palette& palette);
}  // namespace qct::image::pp

namespace qct::image::pp {
tile_bytes_2D_t decode(std::ifstream& file, const std::int32_t image_tile_byte_offset,
                       const palette::Palette& palette) {
  // TODO
  return {};
}
}  // namespace qct::image::pp
