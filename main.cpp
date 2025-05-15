#include <filesystem>
#include <fstream>
#include <functional>
#include <future>
#include <iostream>
#include <ranges>
#include <variant>

#include "CLI11.hpp"

import qct;
import qctexport;

int main(const int argc, char** argv) {
  CLI::App app{"QCT Convert"};
  argv = app.ensure_utf8(argv);

  std::filesystem::path qct_file_path{};
  std::filesystem::path kml_export_file_path{};
  std::filesystem::path geotiff_export_file_path{};
  std::filesystem::path png_export_file_path{};
  app.add_option("qct-file", qct_file_path, "Path to the .qct file")->required();
  app.add_option("--export-kml-path", kml_export_file_path, "Path to optional .kml export");
  app.add_option("--export-geotiff-path", geotiff_export_file_path, "Path to optional GeoTIFF (.tiff) export");
  app.add_option("--export-png-path", png_export_file_path, "Path to optional .png export");
  CLI11_PARSE(app, argc, argv);

  if (exists(qct_file_path)) {
    if (is_regular_file(qct_file_path)) {
      std::ifstream file{qct_file_path, std::ios::binary};
      try {
        const qct::QctFile qct_file = qct::QctFile::parse(qct_file_path);
        std::vector<std::future<void>> export_futures{};
        if (!geotiff_export_file_path.empty()) {
          export_futures.push_back(std::async(std::launch::async, qct::ex::exportTo, qct_file, geotiff_export_file_path,
                                              qct::ex::ExportFormat::GeoTIFF));
        }
        if (!kml_export_file_path.empty()) {
          export_futures.push_back(std::async(std::launch::async, qct::ex::exportTo, qct_file, kml_export_file_path,
                                              qct::ex::ExportFormat::KML));
        }
        if (!png_export_file_path.empty()) {
          export_futures.push_back(std::async(std::launch::async, qct::ex::exportTo, qct_file, png_export_file_path,
                                              qct::ex::ExportFormat::PNG));
        }
        std::ranges::for_each(export_futures, [](auto& future) { future.wait(); });
      } catch (const qct::common::QctException& e) {
        std::cerr << e.what() << std::endl;
      }
    } else {
      std::cerr << "Not a file: " << qct_file_path << std::endl;
    }
  } else {
    std::cerr << "File does not exist:  " << qct_file_path << std::endl;
  }
}
