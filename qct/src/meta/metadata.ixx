module;

#include <chrono>
#include <cstdint>
#include <filesystem>
#include <fstream>
#include <string>

export module qct:meta;

import :meta.extended;
import :meta.magic;
import :meta.outline;
import :meta.version;
import :util.reader;

export namespace qct::meta {
/**
 * +--------+-------------------+--------------------------------------------------------+
 * | Offset | Size (Bytes)      | Content                                                |
 * +--------+-------------------+--------------------------------------------------------+
 * | 0x0000 | 24 x 4            | Meta Data - 24 Integers/Pointers                       |
 * +--------+-------------------+--------------------------------------------------------+
 * <br/>
 * +--------+-------------------+--------------------------------------------------------+
 * | Offset | Data Type         | Content                                                |
 * +--------+-------------------+--------------------------------------------------------+
 * | 0x00   | Integer           | Magic Number                                           |
 * |        |                   | 0x1423D5FE - Quick Chart Information                   |
 * |        |                   | 0x1423D5FF - Quick Chart Map                           |
 * | 0x04   | Integer           | File Format Version                                    |
 * |        |                   | 0x00000002 – Quick Chart                               |
 * |        |                   | 0x00000004 – Quick Chart supporting License Management |
 * |        |                   | 0x20000001 – QC3 Format                                |
 * | 0x08   | Integer           | Width (Tiles)                                          |
 * | 0x0C   | Integer           | Height (Tiles)                                         |
 * | 0x10   | Pointer to String | Long Title                                             |
 * | 0x14   | Pointer to String | Name                                                   |
 * | 0x18   | Pointer to String | Identifier                                             |
 * | 0x1C   | Pointer to String | Edition                                                |
 * | 0x20   | Pointer to String | Revision                                               |
 * | 0x24   | Pointer to String | Keywords                                               |
 * | 0x28   | Pointer to String | Copyright                                              |
 * | 0x2C   | Pointer to String | Scale                                                  |
 * | 0x30   | Pointer to String | Datum                                                  |
 * | 0x34   | Pointer to String | Depths                                                 |
 * | 0x38   | Pointer to String | Heights                                                |
 * | 0x3C   | Pointer to String | Projection                                             |
 * | 0x40   | Integer           | Bit-field Flags                                        |
 * |        |                   | Bit 0 - Must have original file                        |
 * |        |                   | Bit 1 - Allow Calibration                              |
 * | 0x44   | Pointer to String | Original File Name                                     |
 * | 0x48   | Integer           | Original File Size                                     |
 * | 0x4C   | Integer           | Original File Creation Time (seconds since epoch)      |
 * | 0x50   | Integer           | Reserved, set to 0                                     |
 * | 0x54   | Pointer to Struct | Extended Data Structure (see section 6.2.1)            |
 * | 0x58   | Integer           | Number of Map Outline Points                           |
 * | 0x5C   | Pointer to Array  | Map Outline (see section 6.2.2)                        |
 * +--------+-------------------+--------------------------------------------------------+
 */
struct Metadata final {
  static constexpr std::int32_t BYTE_OFFSET{0x0000};

  MagicNumber magic_number{};
  FileFormatVersion file_format_version{};
  std::int32_t width_tiles{0};
  std::int32_t height_tiles{0};
  std::string long_title{};
  std::string name{};
  std::string identifier{};
  std::string edition{};
  std::string revision{};
  std::string keywords{};
  std::string copyright{};
  std::string scale{};
  std::string datum{};
  std::string depths{};
  std::string heights{};
  std::string projection{};
  std::string original_file_name{};
  std::int32_t original_file_size{0};
  std::chrono::system_clock::time_point original_file_creation_time{};
  ExtendedData extended_data{};
  MapOutline map_outline{};

  static Metadata parse(const std::filesystem::path& filepath);

  friend std::ostream& operator<<(std::ostream& os, const Metadata& metadata) {
    os << "Metadata:" << "\n"
       << "\t" << "Magic Number: " << to_string(metadata.magic_number) << "\n"
       << "\t" << "File Format Version: " << to_string(metadata.file_format_version) << "\n"
       << "\t" << "Width Tiles: " << metadata.width_tiles << "\n"
       << "\t" << "Height Tiles: " << metadata.height_tiles << "\n"
       << "\t" << "Long Title: " << metadata.long_title << "\n"
       << "\t" << "Name: " << metadata.name << "\n"
       << "\t" << "Identifier: " << metadata.identifier << "\n"
       << "\t" << "Edition: " << metadata.edition << "\n"
       << "\t" << "Revision: " << metadata.revision << "\n"
       << "\t" << "Keywords: " << metadata.keywords << "\n"
       << "\t" << "Copyright: " << metadata.copyright << "\n"
       << "\t" << "Scale: " << metadata.scale << "\n"
       << "\t" << "Datum: " << metadata.datum << "\n"
       << "\t" << "Depths: " << metadata.depths << "\n"
       << "\t" << "Heights: " << metadata.heights << "\n"
       << "\t" << "Projection: " << metadata.projection << "\n"
       << "\t" << "Original File Name: " << metadata.original_file_name << "\n"
       << "\t" << "Original File Size: " << metadata.original_file_size << "\n"
       << "\t" << "Original File Creation Time: " << metadata.original_file_creation_time << "\n"
       << "\t" << "Extended Data: " << metadata.extended_data << "\n"
       << "\t" << "Map Outline: " << metadata.map_outline;
    return os;
  }
};

Metadata Metadata::parse(const std::filesystem::path& filepath) {
  std::ifstream file{filepath, std::ios::binary};
  Metadata metadata{};
  metadata.magic_number = static_cast<MagicNumber>(util::readInt(file, BYTE_OFFSET + 0x00));
  metadata.file_format_version = static_cast<FileFormatVersion>(util::readInt(file, BYTE_OFFSET + 0x04));
  metadata.width_tiles = util::readInt(file, BYTE_OFFSET + 0x08);
  metadata.height_tiles = util::readInt(file, BYTE_OFFSET + 0x0C);
  metadata.long_title = util::readStringFromPointer(file, BYTE_OFFSET + 0x10);
  metadata.name = util::readStringFromPointer(file, BYTE_OFFSET + 0x14);
  metadata.identifier = util::readStringFromPointer(file, BYTE_OFFSET + 0x18);
  metadata.edition = util::readStringFromPointer(file, BYTE_OFFSET + 0x1C);
  metadata.revision = util::readStringFromPointer(file, BYTE_OFFSET + 0x20);
  metadata.keywords = util::readStringFromPointer(file, BYTE_OFFSET + 0x24);
  metadata.copyright = util::readStringFromPointer(file, BYTE_OFFSET + 0x28);
  metadata.scale = util::readStringFromPointer(file, BYTE_OFFSET + 0x2C);
  metadata.datum = util::readStringFromPointer(file, BYTE_OFFSET + 0x30);
  metadata.depths = util::readStringFromPointer(file, BYTE_OFFSET + 0x34);
  metadata.heights = util::readStringFromPointer(file, BYTE_OFFSET + 0x38);
  metadata.projection = util::readStringFromPointer(file, BYTE_OFFSET + 0x3C);
  metadata.original_file_name = util::readStringFromPointer(file, BYTE_OFFSET + 0x44);
  metadata.original_file_size = util::readInt(file, BYTE_OFFSET + 0x48);
  metadata.original_file_creation_time =
      std::chrono::system_clock::from_time_t(util::readInt(file, BYTE_OFFSET + 0x4C));
  metadata.extended_data = ExtendedData::parse(file, BYTE_OFFSET + 0x54);
  metadata.map_outline = MapOutline::parse(file, BYTE_OFFSET + 0x58, BYTE_OFFSET + 0x5C);
  return metadata;
}

}  // namespace qct::meta
