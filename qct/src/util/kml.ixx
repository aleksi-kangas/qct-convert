module;

#include <filesystem>
#include <fstream>

export module qct:util.kml;

import :common.exception;
import :geo.poly;
import :meta.outline;

namespace qct::util {
export void exportKml(const std::filesystem::path& kml_export_path, const meta::MapOutline& map_outline);

void util::exportKml(const std::filesystem::path& kml_export_path, const meta::MapOutline& map_outline) {
  if (map_outline.points.empty()) {
    throw common::QctException{"No points provided for .kml export"};
  }
  std::ofstream file{kml_export_path};
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
  for (const auto [latitude, longitude] : map_outline.points) {
    file << longitude << "," << latitude << "," << "0" << " ";
  }
  file << map_outline.points[0].longitude << "," << map_outline.points[0].latitude << "," << "0";
  file << "        </coordinates>" << "\n"
       << "      </LineString>" << "\n"
       << "    </Placemark>" << "\n"
       << "  </Document>" << "\n"
       << "</kml>" << "\n";
  file.close();
}

}  // namespace qct::util
