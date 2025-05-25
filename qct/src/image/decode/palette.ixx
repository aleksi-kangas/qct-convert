module;

#include <algorithm>
#include <cmath>
#include <cstdint>
#include <fstream>
#include <stdexcept>
#include <vector>

export module qct:image.decode.palette;

import :common.alias;
import :util.reader;

export namespace qct::image::decode {
/**
 * A subset of the color palette used to define colors in a single tile for efficiency reasons.
 */
struct SubPalette final {
  enum class SizeType {
    NORMAL,  // x
    INVERSE  // 256 - x
  };

  std::int32_t size{};
  std::vector<std::int32_t> palette_indices{};

  [[nodiscard]] std::int32_t bitsRequiredToIndex() const {
    return static_cast<std::int32_t>(std::ceil(std::log2(size)));
  }

  static SubPalette parse(std::ifstream& file, byte_offset_t byte_offset, SizeType size_type);
};

SubPalette SubPalette::parse(std::ifstream& file, const byte_offset_t byte_offset, const SizeType size_type) {
  std::int32_t size{};
  switch (size_type) {
    case SizeType::NORMAL: {
      size = static_cast<std::int32_t>(util::readByte(file, byte_offset));
    } break;
    case SizeType::INVERSE: {
      size = 256 - static_cast<std::int32_t>(util::readByte(file, byte_offset));
    } break;
    default:
      throw std::logic_error{"qct::image::decode::SubPalette::parse: unknown qct::image::decode::SubPalette::SizeType"};
  }
  std::vector palette_indices(size, 0);
  const std::vector<std::uint8_t> bytes = util::readBytes(file, byte_offset + 0x01, size);
  std::ranges::transform(bytes, palette_indices.begin(),
                         [](const std::uint8_t byte) { return static_cast<std::int32_t>(byte); });
  return {.size = size, .palette_indices = palette_indices};
}

}  // namespace qct::image::decode
