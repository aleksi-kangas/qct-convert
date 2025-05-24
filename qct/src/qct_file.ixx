module;

#include <chrono>
#include <filesystem>
#include <future>
#include <iostream>
#include <utility>

export module qct:file;

import :common.exception;
import :georef;
import :image.index;
import :image.tile;
import :palette;
import :meta;
import :meta.magic;
import :meta.version;

export namespace qct {
/**
 * The QCT-file.
 */
struct QctFile final {
  meta::Metadata metadata{};
  georef::Georef georef{};
  palette::Palette palette{};
  image::ImageIndex image_index{};

  [[nodiscard]] std::int32_t height() const { return metadata.height_tiles * image::ImageTile::HEIGHT; }
  [[nodiscard]] std::int32_t width() const { return metadata.width_tiles * image::ImageTile::WIDTH; }

  static QctFile parse(const std::filesystem::path& filepath, bool force_decode = false);

  static void checkMagicNumber(meta::MagicNumber magic_number, bool force_decode = false);
  static void checkFileFormatVersion(meta::FileFormatVersion file_format_version, bool force_decode = false);
};

QctFile QctFile::parse(const std::filesystem::path& filepath, const bool force_decode) {
  auto metadata_future = std::async(std::launch::async, meta::Metadata::parse, filepath);
  auto georef_future = std::async(std::launch::async, georef::Georef::parse, filepath);
  auto palette_future = std::async(std::launch::async, palette::Palette::parse, filepath);
  meta::Metadata metadata = metadata_future.get();
  std::cout << metadata << std::endl;
  checkMagicNumber(metadata.magic_number, force_decode);
  checkFileFormatVersion(metadata.file_format_version, force_decode);
  georef::Georef georef = georef_future.get();
  palette::Palette palette = palette_future.get();
  auto image_index = image::ImageIndex::parse(filepath, metadata, palette);
  return {.metadata = std::move(metadata),
          .georef = std::move(georef),
          .palette = std::move(palette),
          .image_index = std::move(image_index)};
}

void QctFile::checkMagicNumber(const meta::MagicNumber magic_number, const bool force_decode) {
  switch (magic_number) {
    case meta::MagicNumber::QUICK_CHART_INFORMATION:
    case meta::MagicNumber::QUICK_CHART_MAP:
      break;
    default: {
      const std::string unknown_magic_number_message{
          "Unknown magic number, use --force flag to attempt decoding anyway"};
      if (force_decode) {
        std::cerr << unknown_magic_number_message << std::endl;
      } else {
        throw common::QctException{unknown_magic_number_message};
      }
    }
  }
}

void QctFile::checkFileFormatVersion(const meta::FileFormatVersion file_format_version, const bool force_decode) {
  switch (file_format_version) {
    case meta::FileFormatVersion::QUICK_CHART:
    case meta::FileFormatVersion::QUICK_CHART_SUPPORTING_LICENSE_MANAGEMENT:
      break;
    case meta::FileFormatVersion::QC3:
      throw common::QctException{"QC3 file format is not supported"};
    default: {
      const std::string unknown_file_format_version_message{
          "Unknown file format version, use --force flag to attempt decoding anyway"};
      if (force_decode) {
        std::cerr << unknown_file_format_version_message << std::endl;
      } else {
        throw common::QctException{unknown_file_format_version_message};
      }
    }
  }
}

}  // namespace qct
