module;

#include <array>
#include <cstdint>
#include <filesystem>
#include <fstream>
#include <vector>

export module qct:palette;

import :palette.color;
import :util.reader;

export namespace qct::palette {
/**
 * +--------+--------------+------------------------------+
 * | Offset | Size (Bytes) | Content                      |
 * +--------+--------------+------------------------------+
 * | 0x01A0 | 256 x 4      | Palette - 128 of 256 Colours |
 * +--------+--------------+------------------------------+
 */
struct Palette final {
  static constexpr std::int32_t BYTE_OFFSET{0x01A0};
  static constexpr std::int32_t COLOR_COUNT{128};

  std::array<Color, COLOR_COUNT> colors{};

  static Palette parse(const std::filesystem::path& filepath);

  friend std::ostream& operator<<(std::ostream& os, const Palette& palette) {
    os << "Palette:" << "\n";
    for (const auto& [red, green, blue] : palette.colors) {
      // clang-format off
      os << "\t" << "R: " << static_cast<std::int32_t>(red)
         << "\t" << "G: " << static_cast<std::int32_t>(green)
         << "\t" << "B: " << static_cast<std::int32_t>(blue)
         << "\n";
      // clang-format on
    }
    return os;
  }
};

Palette Palette::parse(const std::filesystem::path& filepath) {
  std::ifstream file{filepath, std::ios::binary};
  std::array<Color, COLOR_COUNT> colors{};
  const std::vector<std::uint8_t> bytes = util::readBytes(file, BYTE_OFFSET, COLOR_COUNT * 4);
  for (std::int32_t i = 0; i < COLOR_COUNT; ++i) {
    const std::uint8_t blue = bytes[i * 4 + 0];
    const std::uint8_t green = bytes[i * 4 + 1];
    const std::uint8_t red = bytes[i * 4 + 2];
    colors[i] = Color{.red = red, .green = green, .blue = blue};
  }
  return {.colors = colors};
}

}  // namespace qct::palette
