module;

#include <filesystem>
#include <stdexcept>
#include <variant>

export module qctexport;

import qct;

export import :exception;
export import :geotiff;
export import :kml;
export import :png;

namespace qct::ex {
export enum class ExportFormat {
  GeoTIFF,
  KML,
  PNG
};

export using QctExporter = std::variant<GeoTiffExporter, KmlExporter, PngExporter>;

export QctExporter makeExporter(ExportFormat export_format) {
  switch (export_format) {
    case ExportFormat::GeoTIFF:
      return GeoTiffExporter{};
    case ExportFormat::KML:
      return KmlExporter{};
    case ExportFormat::PNG:
      return PngExporter{};
    default:
      throw std::logic_error{"Unknown export format"};
  }
}

export void exportTo(const QctFile& qct_file, const std::filesystem::path& path, ExportFormat export_format) {
  const QctExporter exporter = makeExporter(export_format);
  std::visit(common::crtp::Overloaded{[&](auto& e) {
               e.exportTo(qct_file, path);
             }},
             exporter);
}

}  // namespace qct::ex
