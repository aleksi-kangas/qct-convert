module;

#include <chrono>
#include <fstream>
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

  static QctFile parse(std::ifstream& file);
};
}  // namespace qct

namespace qct {
QctFile QctFile::parse(std::ifstream& file) {
  auto metadata = meta::Metadata::parse(file, 0x0000);
  std::cout << metadata << std::endl;
  const auto georef_coefficients = geo::GeorefCoefficients::parse(file, 0x0060);
  std::cout << georef_coefficients << std::endl;
  const auto palette = palette::Palette::parse(file, 0x01A0);
  std::cout << palette << std::endl;
  auto image_index = image::ImageIndex::parse(file, 0x45A0, metadata.height_tiles, metadata.width_tiles, palette);

  return {.metadata = std::move(metadata),
          .georef_coefficients = georef_coefficients,
          .palette = palette,
          .image_index = std::move(image_index)};
}

}  // namespace qct
