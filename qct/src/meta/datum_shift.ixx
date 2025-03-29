module;

#include <cstdint>
#include <fstream>

export module qct:meta.datum;

import :util.reader;

export namespace qct::meta {
/**
 *
 */
struct DatumShift final {
  double north{0};
  double east{0};

  static DatumShift parse(std::ifstream& file, std::int32_t pointer_byte_offset);

  friend std::ostream& operator<<(std::ostream& os, const DatumShift& datum_shift) {
    os << "North: " << datum_shift.north << ", East: " << datum_shift.east;
    return os;
  }
};
}  // namespace qct::meta

namespace qct::meta {
DatumShift DatumShift::parse(std::ifstream& file, const std::int32_t pointer_byte_offset) {
  const std::int32_t byte_offset = util::readInt(file, pointer_byte_offset);
  return {.north = util::readDouble(file, byte_offset + 0x00), .east = util::readDouble(file, byte_offset + 0x08)};
}

}  // namespace qct::meta
