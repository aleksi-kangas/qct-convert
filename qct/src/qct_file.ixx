module;

#include <chrono>
#include <filesystem>
#include <future>
#include <iostream>
#include <utility>

export module qct:file;

import :geo.coef;
import :image;
import :image.index;
import :palette;
import :meta;

export namespace qct {
/**
 *
 */
struct QctFile final {
  meta::Metadata metadata{};
  geo::GeorefCoefficients georef_coefficients{};
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
  auto georef_coefficients_future = std::async(std::launch::async, geo::GeorefCoefficients::parse, filepath);
  auto palette_future = std::async(std::launch::async, palette::Palette::parse, filepath);
  meta::Metadata metadata = metadata_future.get();
  std::cout << metadata << std::endl;
  geo::GeorefCoefficients georef_coefficients = georef_coefficients_future.get();
  std::cout << georef_coefficients << std::endl;
  palette::Palette palette = palette_future.get();
  std::cout << palette << std::endl;
  auto image_index = image::ImageIndex::parse(filepath, metadata, palette);
  return {.metadata = std::move(metadata),
          .georef_coefficients = std::move(georef_coefficients),
          .palette = std::move(palette),
          .image_index = std::move(image_index)};
}

}  // namespace qct
