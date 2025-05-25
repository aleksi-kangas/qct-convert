module;

#include <cstdint>
#include <fstream>
#include <vector>

export module qct:util.buffer;

import :common.alias;
import :common.exception;
import :util.reader;

export namespace qct::util {
class DynamicByteBuffer {
 public:
  explicit DynamicByteBuffer(std::ifstream& file, byte_offset_t byte_offset, std::int32_t initial_byte_count);

  /**
   * @return the next byte in the buffer, advances buffer index by one after reading, may read more bytes from the file if necessary
   */
  [[nodiscard]] std::uint8_t nextByte();

 private:
  std::ifstream& file_;
  byte_offset_t file_byte_offset_;
  byte_offset_t next_byte_offset_{0};
  std::int32_t capacity_multiple_;
  std::vector<std::uint8_t> buffer_{};

  [[nodiscard]] std::uint8_t getByte(byte_offset_t buffer_index);

  void readBytes(std::ifstream& file);
};

DynamicByteBuffer::DynamicByteBuffer(std::ifstream& file, const byte_offset_t byte_offset,
                                     const std::int32_t initial_byte_count)
    : file_{file}, file_byte_offset_{byte_offset}, capacity_multiple_{initial_byte_count} {
  buffer_.reserve(capacity_multiple_);
  readBytes(file);
}

std::uint8_t DynamicByteBuffer::nextByte() {
  const std::uint8_t next_byte = getByte(next_byte_offset_++);
  return next_byte;
}
std::uint8_t DynamicByteBuffer::getByte(const byte_offset_t buffer_index) {
  if (buffer_index < buffer_.size()) {
    return buffer_[buffer_index];
  }
  readBytes(file_);
  if (buffer_index < buffer_.size()) {
    return buffer_[buffer_index];
  }
  throw QctException{"Failed to read from buffer."};
}

void DynamicByteBuffer::readBytes(std::ifstream& file) {
  std::vector<std::uint8_t> bytes = readBytesSafe(file, file_byte_offset_, capacity_multiple_);
  buffer_.insert(buffer_.end(), bytes.begin(), bytes.end());
  file_byte_offset_ += static_cast<byte_offset_t>(bytes.size());
}

}  // namespace qct::util
