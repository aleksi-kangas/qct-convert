module;

#include <filesystem>
#include <fstream>

export module qctexport:kml;

import qct;

import :exception;
import :exporter;

export namespace qct::ex {
/**
 * Options for exporting a QCT file to a PNG file.
 */
struct KmlExportOptions final : ExportOptions {
  explicit KmlExportOptions(const std::filesystem::path& path, const bool overwrite) : ExportOptions{path, overwrite} {}
};

/**
 * Exporter for KML files.
 */
class KmlExporter final : public QctExporter<KmlExportOptions> {
 public:
  ~KmlExporter() override = default;

 protected:
  /**
   * Export the given QCT file to the specified path as a KML file.
   *
   * @param qct_file The QCT file to export.
   * @param options The export options for the KML export.
   */
  void exportToImpl(const QctFile& qct_file, const KmlExportOptions& options) const override;
};

void KmlExporter::exportToImpl(const QctFile& qct_file, const KmlExportOptions& options) const {
  const auto& [points] = qct_file.metadata.map_outline;
  if (points.empty()) {
    throw QctExportException{"No points provided for .kml export"};
  }
  std::ofstream file{options.path};
  file << R"(<?xml version="1.0" encoding="UTF-8"?>)" << "\n"
       << "<kml xmlns=\"http://www.opengis.net/kml/2.2\">" << "\n"
       << "  <Document>" << "\n"
       << "    <name>MapOutline</name>" << "\n"
       << "    <description>MapOutline</description>" << "\n"
       << "    <Style>" << "\n"
       << "      <LineStyle>" << "\n"
       << "        <color>ffffff00</color>" << "\n"
       << "        <width>6</width>" << "\n"
       << "      </LineStyle>" << "\n"
       << "    </Style>" << "\n"
       << "    <Placemark>" << "\n"
       << "      <name>MapOutline</name>" << "\n"
       << "      <description>MapOutline</description>" << "\n"
       << "      <LineString>" << "\n"
       << "        <coordinates>" << "\n";
  for (const auto [latitude, longitude] : points) {
    file << longitude << "," << latitude << "," << "0" << " ";
  }
  file << points[0].longitude << "," << points[0].latitude << "," << "0";
  file << "        </coordinates>" << "\n"
       << "      </LineString>" << "\n"
       << "    </Placemark>" << "\n"
       << "  </Document>" << "\n"
       << "</kml>" << "\n";
  file.close();
}

}  // namespace qct::ex
