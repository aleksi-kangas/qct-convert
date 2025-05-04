module;

#include <chrono>
#include <filesystem>
#include <future>
#include <iostream>
#include <utility>

export module qct:file;

import :georef;
import :image.index;
import :palette;
import :meta;

export namespace qct {
/**
 *
 */
struct QctFile final {
  meta::Metadata metadata{};
  georef::Georef georef{};
  palette::Palette palette{};
  image::ImageIndex image_index{};

  [[nodiscard]] std::int32_t height() const { return metadata.height_tiles * image::TILE_HEIGHT; }
  [[nodiscard]] std::int32_t width() const { return metadata.width_tiles * image::TILE_WIDTH; }

  static QctFile parse(const std::filesystem::path& filepath);
};
}  // namespace qct

namespace qct {
QctFile QctFile::parse(const std::filesystem::path& filepath) {
  auto metadata_future = std::async(std::launch::async, meta::Metadata::parse, filepath);
  auto georef_future = std::async(std::launch::async, georef::Georef::parse, filepath);
  auto palette_future = std::async(std::launch::async, palette::Palette::parse, filepath);
  meta::Metadata metadata = metadata_future.get();
  std::cout << metadata << std::endl;
  georef::Georef georef = georef_future.get();
  palette::Palette palette = palette_future.get();
  auto image_index = image::ImageIndex::parse(filepath, metadata, palette);
  return {.metadata = std::move(metadata),
          .georef = std::move(georef),
          .palette = std::move(palette),
          .image_index = std::move(image_index)};
}

}  // namespace qct
