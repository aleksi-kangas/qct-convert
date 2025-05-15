module;

#include <filesystem>
#include <fstream>

export module qctexport:kml;

import qct;

import :exception;
import :exporter;

namespace qct::ex {
class KmlExporter final : public AbstractExporter<KmlExporter> {
 public:
  /**
   * Export the given QCT file to the specified path as a KML file.
   *
   * @param qct_file The QCT file to export.
   * @param path The path where the exported KML file should be saved.
   */
  void exportTo(const QctFile& qct_file, const std::filesystem::path& path) const;
};

void KmlExporter::exportTo(const QctFile& qct_file, const std::filesystem::path& path) const {
  const auto& [points] = qct_file.metadata.map_outline;
  if (points.empty()) {
    throw QctExportException{"No points provided for .kml export"};
  }
  std::ofstream file{path};
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
