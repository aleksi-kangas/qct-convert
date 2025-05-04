module;

#include <array>
#include <cstdint>
#include <format>
#include <fstream>
#include <string>
#include <vector>

export module qct:util.reader;

import :common.exception;

export namespace qct::util {
/**
 * Reads a byte from the given byte offset.
 * @param file to read from
 * @param byte_offset byte offset to read from
 * @return read byte
 */
std::uint8_t readByte(std::ifstream& file, std::int32_t byte_offset);

/**
 * Reads multiple bytes from the given byte offset.
 * @param file to read from
 * @param byte_offset byte offset to read from
 * @param count the amount of bytes to read
 * @return read bytes
 */
std::vector<std::uint8_t> readBytes(std::ifstream& file, std::int32_t byte_offset, std::int32_t count);

/**
 * Reads an integer stored as little-endian from the given byte offset.
 * @param file to read from
 * @param byte_offset byte offset to read from
 * @return read integer
 */
std::int32_t readInt(std::ifstream& file, std::int32_t byte_offset);

/**
 * Reads a double (8 byte IEEE-754) from the given byte offset.
 * @param file to read from
 * @param byte_offset byte offset to read from
 * @return read double
 */
double readDouble(std::ifstream& file, std::int32_t byte_offset);

/**
 * Reads multiple doubles (8 byte IEEE-754) from the given byte offset.
 * @param file to read from
 * @param byte_offset byte offset to read from
 * @param count the amount of doubles to read
 * @return read doubles
 */
std::vector<double> readDoubles(std::ifstream& file, std::int32_t byte_offset, std::int32_t count);

/**
 * Reads a null-terminated string from the given byte offset.
 * @param file to read from
 * @param byte_offset byte offset to read from
 * @return read string
 */
std::string readString(std::ifstream& file, std::int32_t byte_offset);

/**
 * Reads a null-terminated string by first reading the string pointer from the given byte offset,
 * and then reading the string from the pointed byte offset.
 * @param file to read from
 * @param pointer_byte_offset byte offset of the pointer to read from
 * @return read string
 */
std::string readStringFromPointer(std::ifstream& file, std::int32_t pointer_byte_offset);
}  // namespace qct::util

namespace qct::util {
std::uint8_t util::readByte(std::ifstream& file, const std::int32_t byte_offset) {
  return readBytes(file, byte_offset, 1)[0];
}

std::vector<std::uint8_t> util::readBytes(std::ifstream& file, const std::int32_t byte_offset,
                                          const std::int32_t count) {
  file.seekg(byte_offset);
  if (!file.good()) {
    throw common::QctException{std::format("Failed to seek to offset={}", byte_offset)};
  }
  std::vector<std::uint8_t> bytes(count);
  file.read(reinterpret_cast<char*>(bytes.data()), count);
  if (file.gcount() != count) {
    throw common::QctException{std::format("Failed to read n={} bytes at offset={}", count, byte_offset)};
  }
  return bytes;
}

std::int32_t util::readInt(std::ifstream& file, const std::int32_t byte_offset) {
  file.seekg(byte_offset);
  if (!file.good()) {
    throw common::QctException{std::format("Failed to seek to offset={}", byte_offset)};
  }
  std::array<unsigned char, 4> bytes{};
  file.read(reinterpret_cast<char*>(bytes.data()), bytes.size());
  if (file.gcount() != bytes.size()) {
    throw common::QctException{std::format("Failed to read integer at offset={}", byte_offset)};
  }
  return static_cast<std::int32_t>(bytes[0]) << 0 | static_cast<std::int32_t>(bytes[1]) << 8 |
         static_cast<std::int32_t>(bytes[2]) << 16 | static_cast<std::int32_t>(bytes[3]) << 24;
}

double util::readDouble(std::ifstream& file, const std::int32_t byte_offset) {
  file.seekg(byte_offset);
  if (!file.good()) {
    throw common::QctException{std::format("Failed to seek to offset={}", byte_offset)};
  }
  double value{0};
  file.read(reinterpret_cast<char*>(&value), 0x08);
  if (file.gcount() != 0x08) {
    throw common::QctException{std::format("Failed to read double at offset={}", byte_offset)};
  }
  return value;
}

std::vector<double> util::readDoubles(std::ifstream& file, const std::int32_t byte_offset, const std::int32_t count) {
  std::vector<double> doubles(count, 0);
  for (std::int32_t i = 0; i < count; ++i) {
    doubles[i] = readDouble(file, byte_offset + i * 0x08);
  }
  return doubles;
}

std::string util::readString(std::ifstream& file, const std::int32_t byte_offset) {
  file.seekg(byte_offset);
  if (!file.good()) {
    throw common::QctException{std::format("Failed to seek to offset={}", byte_offset)};
  }
  std::string result{};
  unsigned char ch{0};
  while (file.get(reinterpret_cast<char&>(ch))) {
    if (ch == '\0') {
      break;
    }
    result.push_back(static_cast<char>(ch));
  }
  if (file.eof() && result.back() != '\0') {
    throw common::QctException{"Reached EOF before NULL."};
  }
  return result;
}

std::string util::readStringFromPointer(std::ifstream& file, const std::int32_t pointer_byte_offset) {
  const std::int32_t byteOffset = readInt(file, pointer_byte_offset);
  return byteOffset != 0 ? readString(file, byteOffset) : "";
}

}  // namespace qct::util
