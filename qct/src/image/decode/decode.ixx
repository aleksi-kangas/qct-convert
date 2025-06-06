module;

#include <stdexcept>
#include <variant>

export module qct:image.decode;

import :image.decode.huffman;
import :image.decode.pp;
import :image.decode.rle;
import :image.tile;
import :palette;

export namespace qct::image::decode {
/**
 * A variant type for image tile decoders.
 */
using ImageTileDecoder = std::variant<HuffmanImageTileDecoder, PixelPackingImageTileDecoder, RLEImageTileDecoder>;

/**
 * Make an image tile decoder for the given encoding and palette.
 * @param encoding the encoding of the image tile
 * @param palette the palette of the image tile
 * @return an image tile decoder for the given encoding and palette
 */
ImageTileDecoder makeImageTileDecoder(const ImageTile::Encoding encoding, const palette::Palette& palette) {
  switch (encoding) {
    case ImageTile::Encoding::HUFFMAN_CODING:
      return HuffmanImageTileDecoder{palette};
    case ImageTile::Encoding::PIXEL_PACKING:
      return PixelPackingImageTileDecoder{palette};
    case ImageTile::Encoding::RUN_LENGTH_ENCODING:
      return RLEImageTileDecoder{palette};
    default:
      throw std::logic_error{"Unknown encoding"};
  }
}

}  // namespace qct::image::decode
