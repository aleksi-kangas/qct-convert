#include <filesystem>
#include <fstream>
#include <iostream>

#include "CLI11.hpp"

import qct;
import qctexport;

int main(const int argc, char** argv) {
  CLI::App app{"QCT Convert"};
  argv = app.ensure_utf8(argv);

  std::filesystem::path qct_file_path{};
  std::filesystem::path kml_export_file_path{};
  std::filesystem::path png_export_file_path{};
  app.add_option("-p,--path", qct_file_path, "Path to the .qct file");
  app.add_option("--export-kml-path", kml_export_file_path, "Path to optional .kml export");
  app.add_option("--export-png-path", png_export_file_path, "Path to optional .png export");
  CLI11_PARSE(app, argc, argv);

  if (exists(qct_file_path)) {
    if (is_regular_file(qct_file_path)) {
      std::ifstream file{qct_file_path, std::ios::binary};
      try {
        const qct::QctFile qct_file = qct::QctFile::parse(file);
        if (!kml_export_file_path.empty()) {
          qct::ex::kml::exportKml(kml_export_file_path, qct_file);
        }
        if (!png_export_file_path.empty()) {
          qct::ex::png::exportPng(png_export_file_path, qct_file);
        }
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