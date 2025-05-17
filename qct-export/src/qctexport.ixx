module;

#include <filesystem>
#include <iostream>
#include <memory>
#include <stdexcept>

export module qctexport;

import qct;

export import :exception;
export import :geotiff;
export import :kml;
export import :png;

namespace qct::ex {
/**
 * Construct an exporter given the options.
 * @tparam O export options type
 * @return an exporter
 */
export template <typename O>
std::unique_ptr<QctExporter<O>> makeExporter() {
  if constexpr (std::is_same_v<O, KmlExportOptions>) {
    return std::make_unique<KmlExporter>();
  } else if constexpr (std::is_same_v<O, GeoTiffExportOptions>) {
    return std::make_unique<GeoTiffExporter>();
  } else if constexpr (std::is_same_v<O, PngExportOptions>) {
    return std::make_unique<PngExporter>();
  }
  throw std::logic_error{"Unknown ExportOptions"};
}

/**
 * Export the given QCT file using the given export options.
 * @tparam O export options type
 * @param qct_file the QCT file to be exported
 * @param export_options the export options
 */
export template <typename O>
void exportToFormat(const QctFile& qct_file, const O& export_options) {
  const std::unique_ptr<QctExporter<O>> exporter = makeExporter<O>();
  try {
    exporter->exportTo(qct_file, export_options);
  } catch (const QctExportException& e) {
    std::cerr << e.what() << std::endl;
  }
}
}  // namespace qct::ex
