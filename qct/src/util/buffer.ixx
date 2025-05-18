module;

#include <cstdint>
#include <fstream>
#include <vector>

export module qct:util.buffer;

import :common.exception;
import :util.reader;

export namespace qct::util {
class DynamicByteBuffer {
 public:
  explicit DynamicByteBuffer(std::ifstream& file, std::int32_t byte_offset, std::int32_t initial_byte_count);

  [[nodiscard]] std::uint8_t getByte(std::int32_t index);

  [[nodiscard]] std::uint8_t nextByte();

 private:
  std::ifstream& file_;
  std::int32_t file_byte_offset_;
  std::int32_t next_byte_offset_{0};
  std::int32_t capacity_multiple_;
  std::vector<std::uint8_t> buffer_{};

  void readBytes(std::ifstream& file);
};

DynamicByteBuffer::DynamicByteBuffer(std::ifstream& file, const std::int32_t byte_offset,
                                     const std::int32_t initial_byte_count)
    : file_{file}, file_byte_offset_{byte_offset}, capacity_multiple_{initial_byte_count} {
  buffer_.reserve(capacity_multiple_);
  readBytes(file);
}

std::uint8_t DynamicByteBuffer::getByte(const std::int32_t index) {
  if (index < buffer_.size()) {
    return buffer_[index];
  }
  readBytes(file_);
  if (index < buffer_.size()) {
    return buffer_[index];
  }
  throw common::QctException{"Failed to read from buffer."};
}
std::uint8_t DynamicByteBuffer::nextByte() {
  const std::uint8_t next_byte = getByte(next_byte_offset_++);
  return next_byte;
}

void DynamicByteBuffer::readBytes(std::ifstream& file) {
  std::vector<std::uint8_t> bytes = readBytesSafe(file, file_byte_offset_, capacity_multiple_);
  buffer_.insert(buffer_.end(), bytes.begin(), bytes.end());
  file_byte_offset_ += static_cast<std::int32_t>(bytes.size());
}

}  // namespace qct::util
