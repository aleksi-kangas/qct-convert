module;

#include <complex>
#include <cstdint>
#include <format>
#include <fstream>
#include <vector>

export module qct:image.decode.huffman;

import :common.exception;
import :image.decoder;
import :image.tile;
import :palette;
import :palette.color;
import :util.buffer;
import :util.reader;

namespace qct::image::decode {
/**
 * Huffman code book.
 */
class HuffmanCodeBook {
 public:
  [[nodiscard]] bool isColor() const;
  [[nodiscard]] bool isColor(std::int32_t node) const;

  [[nodiscard]] bool isFarBranch() const;
  [[nodiscard]] bool isFarBranch(std::int32_t node) const;

  [[nodiscard]] bool isNearBranch() const;
  [[nodiscard]] bool isNearBranch(std::int32_t node) const;

  [[nodiscard]] bool isValid() const;

  [[nodiscard]] palette::Color getColor(const palette::Palette& palette) const;
  [[nodiscard]] palette::Color getColor(const palette::Palette& palette, std::int32_t node) const;

  [[nodiscard]] std::int32_t size() const;

  void resetPointer();
  void step(bool bit);

  static HuffmanCodeBook parse(util::DynamicByteBuffer& dynamic_byte_buffer);

 private:
  std::vector<std::uint8_t> bytes_{};
  std::int32_t pointer_{0};

  [[nodiscard]] std::int32_t farBranchJumpSize(std::int32_t node) const;
  [[nodiscard]] std::int32_t nearBranchJumpSize(std::int32_t node) const;
};

/**
 * A decoder for image tiles using Huffman Coding.
 */
class HuffmanImageTileDecoder final : public AbstractImageTileDecoder<HuffmanImageTileDecoder> {
 public:
  explicit HuffmanImageTileDecoder(const palette::Palette& palette) : AbstractImageTileDecoder{palette} {}
  ~HuffmanImageTileDecoder() override = default;

  /**
   * Decode bytes of an image tile using Huffman Coding.
   * @param file to read from
   * @param image_tile_byte_offset the byte offset of the tile in the image file
   * @return the tile bytes
   */
  [[nodiscard]] ImageTile::bytes_2d_t decodeTileBytes(std::ifstream& file, std::int32_t image_tile_byte_offset) const;
};

bool HuffmanCodeBook::isColor() const {
  return isColor(pointer_);
}

bool HuffmanCodeBook::isColor(const std::int32_t node) const {
  return static_cast<std::int32_t>(bytes_[node]) < 128;
}
bool HuffmanCodeBook::isFarBranch() const {
  return isFarBranch(pointer_);
}

bool HuffmanCodeBook::isFarBranch(const std::int32_t node) const {
  return static_cast<std::int32_t>(bytes_[node]) == 128;
}

bool HuffmanCodeBook::isNearBranch() const {
  return isNearBranch(pointer_);
}

bool HuffmanCodeBook::isNearBranch(const std::int32_t node) const {
  return static_cast<std::int32_t>(bytes_[node]) > 128;
}

bool HuffmanCodeBook::isValid() const {
  if (size() == 0)
    return false;
  if (size() == 1)
    return true;
  for (std::int32_t i = 0; i < size(); ++i) {
    if (isColor(i))
      continue;
    if (isFarBranch(i)) {
      if (i + 2 >= size())
        return false;
      if (i + farBranchJumpSize(i) >= size())
        return false;
      i += 2;
    } else if (isNearBranch(i)) {
      if (i + nearBranchJumpSize(i) >= size())
        return false;
    } else {
      return false;
    }
  }
  return true;
}

palette::Color HuffmanCodeBook::getColor(const palette::Palette& palette) const {
  return getColor(palette, pointer_);
}
palette::Color HuffmanCodeBook::getColor(const palette::Palette& palette, const std::int32_t node) const {
  if (!isColor(node))
    throw common::QctException{std::format("Attempting to get color from non-color node={}", node)};
  return palette.colors[static_cast<std::int32_t>(bytes_[node])];
}

std::int32_t HuffmanCodeBook::size() const {
  return static_cast<std::int32_t>(bytes_.size());
}

void HuffmanCodeBook::resetPointer() {
  pointer_ = 0;
}

void HuffmanCodeBook::step(const bool bit) {
  if (bit) {  // Right, i.e. jump
    if (isNearBranch(pointer_)) {
      pointer_ += nearBranchJumpSize(pointer_);
    } else if (isFarBranch()) {
      pointer_ += farBranchJumpSize(pointer_);
    } else {
      throw common::QctException{"Attempting to step in a non-branch node"};
    }
  } else {  // Left, i.e. no jump
    if (isFarBranch(pointer_)) {
      pointer_ += 3;
    } else if (isNearBranch(pointer_)) {
      ++pointer_;
    } else {
      throw common::QctException{"Attempting to step in a non-branch node"};
    }
  }
}

HuffmanCodeBook HuffmanCodeBook::parse(util::DynamicByteBuffer& dynamic_byte_buffer) {
  HuffmanCodeBook tree{};
  tree.bytes_.reserve(256);
  std::int32_t color_count{0};
  std::int32_t branch_count{0};
  while (color_count <= branch_count) {
    tree.bytes_.push_back(dynamic_byte_buffer.nextByte());
    if (tree.isFarBranch(tree.size() - 1)) {
      tree.bytes_.push_back(dynamic_byte_buffer.nextByte());
      tree.bytes_.push_back(dynamic_byte_buffer.nextByte());
      ++branch_count;
    } else if (tree.isNearBranch(tree.size() - 1)) {
      ++branch_count;
    } else {
      ++color_count;
    }
  }
  if (!tree.isValid()) {
    throw common::QctException{"Invalid Huffman tree"};
  }
  return tree;
}

std::int32_t HuffmanCodeBook::farBranchJumpSize(const std::int32_t node) const {
  return 65537 - (256 * static_cast<std::int32_t>(bytes_[node + 2]) + static_cast<std::int32_t>(bytes_[node + 1])) + 2;
}
std::int32_t HuffmanCodeBook::nearBranchJumpSize(const std::int32_t node) const {
  return 257 - static_cast<std::int32_t>(bytes_[node]);
}
ImageTile::bytes_2d_t HuffmanImageTileDecoder::decodeTileBytes(std::ifstream& file,
                                                               const std::int32_t image_tile_byte_offset) const {
  ImageTile::bytes_2d_t tile{};
  util::DynamicByteBuffer dynamic_byte_buffer{file, image_tile_byte_offset + 1, 4096};
  auto tree = HuffmanCodeBook::parse(dynamic_byte_buffer);
  if (tree.size() == 1) {
    const auto [red, green, blue] = tree.getColor(palette, 0);
    for (std::int32_t pixel_index = 0; pixel_index < ImageTile::PIXEL_COUNT; ++pixel_index) {
      const std::int32_t y = pixel_index * palette::COLOR_CHANNELS / ImageTile::ROW_BYTE_COUNT;
      const std::int32_t x = pixel_index * palette::COLOR_CHANNELS % ImageTile::ROW_BYTE_COUNT;
      tile[y][x + 0] = red;
      tile[y][x + 1] = green;
      tile[y][x + 2] = blue;
    }
    return tile;
  }
  std::uint8_t current_byte = dynamic_byte_buffer.nextByte();
  std::int32_t bit_count{8};
  std::int32_t pixel_index{0};
  tree.resetPointer();
  while (pixel_index < ImageTile::PIXEL_COUNT) {
    if (tree.isColor()) {
      const std::int32_t y = pixel_index * palette::COLOR_CHANNELS / ImageTile::ROW_BYTE_COUNT;
      const std::int32_t x = pixel_index * palette::COLOR_CHANNELS % ImageTile::ROW_BYTE_COUNT;
      const auto [red, green, blue] = tree.getColor(palette);
      tile[y][x + 0] = red;
      tile[y][x + 1] = green;
      tile[y][x + 2] = blue;
      ++pixel_index;
      tree.resetPointer();
      continue;
    }
    const bool bit = current_byte & 1;
    tree.step(bit);

    current_byte >>= 1;
    --bit_count;
    if (bit_count == 0) {
      current_byte = dynamic_byte_buffer.nextByte();
      bit_count = 8;
    }
  }
  return tile;
}

}  // namespace qct::image::decode
