module;

#include <cstdint>
#include <fstream>
#include <string>

export module qct:meta.extended;

import :meta.datum;
import :util.reader;

export namespace qct::meta {
/**
 * +--------+-------------------+---------------------------------------------------+
 * | Offset | Data Type         | Content                                           |
 * +--------+-------------------+---------------------------------------------------+
 * | 0x00   | Pointer to String | Map Type                                          |
 * | 0x04   | Pointer to Array  | Datum Shift (see section 6.2.3)                   |
 * | 0x08   | Pointer to String | Disk Name                                         |
 * | 0x0C   | Integer           | Reserved, set to 0                                |
 * | 0x10   | Integer           | Reserved, set to 0                                |
 * | 0x14   | Pointer to Struct | License Information (Optional, see section 6.2.4) |
 * | 0x18   | Pointer to String | Associated Data                                   |
 * | 0x1C   | Pointer to Struct | Digital Map Shop (Optional, see section 6.2.5)    |
 * +--------+-------------------+---------------------------------------------------+
 */
struct ExtendedData final {
  std::string map_type{};
  DatumShift datum_shift{};
  std::string disk_name{};
  std::string associated_data{};

  static ExtendedData parse(std::ifstream& file, std::int32_t pointer_byte_offset);

  friend std::ostream& operator<<(std::ostream& os, const ExtendedData& extended_data) {
    os << "\n"
       << "\t\t" << "Map Type: " << extended_data.map_type << "\n"
       << "\t\t" << "Datum Shift: " << extended_data.datum_shift << "\n"
       << "\t\t" << "Disk Name: " << extended_data.disk_name << "\n"
       << "\t\t" << "Associated Data: " << extended_data.associated_data;
    return os;
  }
};

ExtendedData ExtendedData::parse(std::ifstream& file, const std::int32_t pointer_byte_offset) {
  const std::int32_t byte_offset = util::readInt(file, pointer_byte_offset);
  return {.map_type = util::readStringFromPointer(file, byte_offset + 0x00),
          .datum_shift = DatumShift::parse(file, byte_offset + 0x04),
          .disk_name = util::readStringFromPointer(file, byte_offset + 0x08),
          .associated_data = util::readStringFromPointer(file, byte_offset + 0x18)};
}

}  // namespace qct::meta
