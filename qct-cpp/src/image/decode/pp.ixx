module;

#include <cstdint>
#include <fstream>
#include <iostream>

export module qct:image.decode.pp;

import :common.alias;
import :image.decoder;
import :image.tile;
import :palette;
import :util.reader;

export namespace qct::image::decode {
/**
 * A decoder for image tiles using Pixel Packing. NOTE: This decoder has not been implemented yet.
 */
class PixelPackingImageTileDecoder final : public AbstractImageTileDecoder<PixelPackingImageTileDecoder> {
 public:
  explicit PixelPackingImageTileDecoder(const palette::Palette& palette) : AbstractImageTileDecoder{palette} {}
  ~PixelPackingImageTileDecoder() override = default;

  [[nodiscard]] ImageTile::bytes_2d_t decodeTileBytes(std::ifstream& file,
                                                      const byte_offset_t image_tile_byte_offset) const {
    // TODO
    std::cerr << "Pixel packing decoder not implemented, output tile shall be empty" << std::endl;
    return {};
  }
};
}  // namespace qct::image::decode
