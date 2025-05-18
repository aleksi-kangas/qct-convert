module;

#include <cstdint>
#include <fstream>

export module qct:image.decode.pp;

import :image.decoder;
import :palette;
import :util.reader;

namespace qct::image::decode {
/**
 * A decoder for image tiles using Pixel Packing. NOTE: This decoder has not been implemented yet.
 */
class PixelPackingImageTileDecoder final : public AbstractImageTileDecoder<PixelPackingImageTileDecoder> {
 public:
  explicit PixelPackingImageTileDecoder(const palette::Palette& palette) : AbstractImageTileDecoder{palette} {}
  ~PixelPackingImageTileDecoder() override = default;

  [[nodiscard]] ImageTile::bytes_2d_t decodeTileBytes(std::ifstream& file,
                                                      const std::int32_t image_tile_byte_offset) const {
    // TODO
    return {};
  }
};
}  // namespace qct::image::decode
