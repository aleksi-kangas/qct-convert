module;

#include <filesystem>
#include <iostream>
#include <memory>
#include <stdexcept>

export module qctexport;

import qct;

export import :exception;
export import :exporter;
export import :geotiff;
export import :kml;
export import :png;

export namespace qct::ex {
/**
 * Export the given QCT file using the given export options.
 * @tparam O export options type
 * @param qct_file the QCT file to be exported
 * @param export_options the export options
 */
template <typename O>
void exportToFormat(const QctFile& qct_file, const O& export_options) {
  try {
    if constexpr (std::is_same_v<O, GeoTiffExportOptions>) {
      const GeoTiffExporter exporter{};
      exporter.exportTo(qct_file, export_options);
    } else if constexpr (std::is_same_v<O, KmlExportOptions>) {
      const KmlExporter exporter{};
      exporter.exportTo(qct_file, export_options);
    } else if constexpr (std::is_same_v<O, PngExportOptions>) {
      const PngExporter exporter{};
      exporter.exportTo(qct_file, export_options);
    }
  } catch (const QctExportException& e) {
    std::cerr << "Failed to export: " << e.what() << std::endl;
  }
}
}  // namespace qct::ex
