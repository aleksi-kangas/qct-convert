module;

#include <chrono>
#include <cstdint>
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
 *
 */
struct Metadata final {
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

  static Metadata parse(std::ifstream& file, std::int32_t byte_offset);

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
}  // namespace qct::meta

namespace qct::meta {
Metadata Metadata::parse(std::ifstream& file, const std::int32_t byte_offset) {
  file.seekg(0, std::ios::beg);
  Metadata metadata{};
  metadata.magic_number = static_cast<MagicNumber>(util::readInt(file, byte_offset + 0x00));
  metadata.file_format_version = static_cast<FileFormatVersion>(util::readInt(file, byte_offset + 0x04));
  metadata.width_tiles = util::readInt(file, byte_offset + 0x08);
  metadata.height_tiles = util::readInt(file, byte_offset + 0x0C);
  metadata.long_title = util::readStringFromPointer(file, byte_offset + 0x10);
  metadata.name = util::readStringFromPointer(file, byte_offset + 0x14);
  metadata.identifier = util::readStringFromPointer(file, byte_offset + 0x18);
  metadata.edition = util::readStringFromPointer(file, byte_offset + 0x1C);
  metadata.revision = util::readStringFromPointer(file, byte_offset + 0x20);
  metadata.keywords = util::readStringFromPointer(file, byte_offset + 0x24);
  metadata.copyright = util::readStringFromPointer(file, byte_offset + 0x28);
  metadata.scale = util::readStringFromPointer(file, byte_offset + 0x2C);
  metadata.datum = util::readStringFromPointer(file, byte_offset + 0x30);
  metadata.depths = util::readStringFromPointer(file, byte_offset + 0x34);
  metadata.heights = util::readStringFromPointer(file, byte_offset + 0x38);
  metadata.projection = util::readStringFromPointer(file, byte_offset + 0x3C);
  metadata.original_file_name = util::readStringFromPointer(file, byte_offset + 0x44);
  metadata.original_file_size = util::readInt(file, byte_offset + 0x48);
  metadata.original_file_creation_time =
      std::chrono::system_clock::from_time_t(util::readInt(file, byte_offset + 0x4C));
  metadata.extended_data = ExtendedData::parse(file, byte_offset + 0x54);
  metadata.map_outline = MapOutline::parse(file, byte_offset + 0x58, byte_offset + 0x5C);
  return metadata;
}

}  // namespace qct::meta
