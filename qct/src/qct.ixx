module;

#include <chrono>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <optional>
#include <utility>

export module qct;

// common
export import :common.exception;

// geo
export import :geo.coef;
export import :geo.poly;

// metadata
export import :meta;
export import :meta.datum;
export import :meta.extended;
export import :meta.magic;
export import :meta.outline;
export import :meta.version;

//  util
export import :util.kml;
export import :util.reader;

namespace qct {
export struct QctFile final {
  meta::Metadata metadata{};
  geo::GeorefCoefficients georef_coefficients{};

  static QctFile parse(std::ifstream& file, const std::optional<std::filesystem::path>& kml_export_path);
};

QctFile QctFile::parse(std::ifstream& file, const std::optional<std::filesystem::path>& kml_export_path) {
  auto metadata = meta::Metadata::parse(file, 0x0000);
  std::cout << metadata << std::endl;
  if (kml_export_path.has_value()) {
    util::exportKml(kml_export_path.value(), metadata.map_outline);
  }
  const auto georef_coefficients = geo::GeorefCoefficients::parse(file, 0x0060);
  std::cout << georef_coefficients << std::endl;
  return {.metadata = std::move(metadata), .georef_coefficients = georef_coefficients};
}

}  // namespace qct
