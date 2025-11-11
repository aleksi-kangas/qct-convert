module;

#include <cstdint>

export module qct:palette.color;

export namespace qct::palette {
constexpr std::int32_t COLOR_CHANNELS = 3;
struct Color final {
  std::uint8_t red{};
  std::uint8_t green{};
  std::uint8_t blue{};
};
}  // namespace qct::palette
