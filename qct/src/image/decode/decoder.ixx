module;

#include <array>
#include <concepts>
#include <cstdint>
#include <fstream>

export module qct:image.decoder;

import :image.tile;
import :palette;

namespace qct::image::decode {
/**
 * An abstract image tile decoder using CRTP.
 * @tparam C the concrete class type
 */
template <class C>
class AbstractImageTileDecoder;

/**
 * A concept for image tile decoders.
 * @tparam T the type of decoder
 */
template <typename T>
concept ImageTileBytesDecoder = requires(T t) {
  { t.decodeTileBytes(std::declval<std::ifstream&>(), std::declval<std::int32_t>()) } -> std::same_as<ImageTile::bytes_2d_t>;
  requires std::derived_from<T, AbstractImageTileDecoder<T>>;
};

/**
 * An abstract image tile decoder using CRTP.
 * @tparam C the concrete class type
 */
template <class C>
class AbstractImageTileDecoder {
 public:
  virtual ~AbstractImageTileDecoder() = default;

  [[nodiscard]] ImageTile::bytes_2d_t decodeTile(std::ifstream& file, std::int32_t image_tile_byte_offset) const {
    static_assert(ImageTileBytesDecoder<C>, "C must be a concrete class type that implements ImageTileBytesDecoder.");
    ImageTile::bytes_2d_t tile_bytes = underlying().decodeTileBytes(file, image_tile_byte_offset);
    deinterlaceRows(tile_bytes);
    return tile_bytes;
  }

 protected:
  explicit AbstractImageTileDecoder(const palette::Palette& palette) : palette{palette} {}

  const palette::Palette& palette;

 private:
  friend C;

  [[nodiscard]] C& underlying() { return static_cast<C&>(*this); }
  [[nodiscard]] const C& underlying() const { return static_cast<const C&>(*this); }

  /**
   * Deinterlace rows of the given tile bytes interlaced using a bit-reverse sequence.
   * @param tile_bytes_2d the tile bytes to deinterlace
   * @return the deinterlaced tile bytes
   */
  static void deinterlaceRows(ImageTile::bytes_2d_t& tile_bytes_2d) {
    std::array<std::int32_t, ImageTile::HEIGHT> deinterlaced_row_sequence{};
    for (std::int32_t i = 0; i < ImageTile::HEIGHT; ++i) {
      constexpr std::array interlaced_row_sequence = {0, 32, 16, 48, 8,  40, 24, 56, 4, 36, 20, 52, 12, 44, 28, 60,
                                                      2, 34, 18, 50, 10, 42, 26, 58, 6, 38, 22, 54, 14, 46, 30, 62,
                                                      1, 33, 17, 49, 9,  41, 25, 57, 5, 37, 21, 53, 13, 45, 29, 61,
                                                      3, 35, 19, 51, 11, 43, 27, 59, 7, 39, 23, 55, 15, 47, 31, 63};

      deinterlaced_row_sequence[interlaced_row_sequence[i]] = i;
    }
    const ImageTile::bytes_2d_t temp_tile_bytes_2d = tile_bytes_2d;
    for (std::int32_t i = 0; i < ImageTile::HEIGHT; ++i) {
      tile_bytes_2d[deinterlaced_row_sequence[i]] = temp_tile_bytes_2d[i];
    }
  }
};

}  // namespace qct::image::decode
