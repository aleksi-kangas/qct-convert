module;

#include <array>
#include <cstdint>
#include <span>
#include <vector>

export module qct:image;

import :palette.color;

export namespace qct::image {
constexpr std::int32_t TILE_HEIGHT = 64;
constexpr std::int32_t TILE_WIDTH = 64;
constexpr std::int32_t TILE_PIXEL_COUNT = TILE_HEIGHT * TILE_WIDTH;
constexpr std::int32_t TILE_BYTE_COUNT = TILE_PIXEL_COUNT * palette::COLOR_CHANNELS;
constexpr std::int32_t TILE_ROW_BYTE_COUNT = TILE_WIDTH * palette::COLOR_CHANNELS;

using image_bytes_t = std::vector<std::uint8_t>;
using tile_bytes_t = std::array<std::uint8_t, TILE_BYTE_COUNT>;
using tile_bytes_2D_t = std::array<std::array<std::uint8_t, TILE_ROW_BYTE_COUNT>, TILE_HEIGHT>;
using tile_bytes_view_t = std::span<const tile_bytes_t>;
}  // namespace qct::image
