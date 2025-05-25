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

void exports(const qct::QctFile& qct_file, const qct::ex::GeoTiffExportOptions& geotiff_export_options,
             const qct::ex::KmlExportOptions& kml_export_options, const qct::ex::PngExportOptions& png_export_options);

int main(const int argc, char** argv) {
  CLI::App app{"QCT Convert"};
  argv = app.ensure_utf8(argv);

  bool force_decode{false};
  std::filesystem::path qct_file_path{};
  std::filesystem::path kml_export_path{};
  std::filesystem::path geotiff_export_path{};
  std::filesystem::path png_export_path{};
  auto geotiff_georef_method{qct::ex::GeoTiffExportOptions::GeorefMethod::AUTOMATIC};
  std::map<std::string, qct::ex::GeoTiffExportOptions::GeorefMethod> georef_method_mapper{
      {"auto", qct::ex::GeoTiffExportOptions::GeorefMethod::AUTOMATIC},
      {"gcp", qct::ex::GeoTiffExportOptions::GeorefMethod::GCP},
      {"linear", qct::ex::GeoTiffExportOptions::GeorefMethod::LINEAR}};

  app.add_option("qct-file-path", qct_file_path, "Path to the .qct file")->required();
  app.add_flag("-f, --force", force_decode, "Force try to decode the .qct file, even if metadata is invalid");
  app.add_option("--export-kml-path", kml_export_path, "Path to optional .kml export");
  app.add_option("--export-geotiff-path", geotiff_export_path, "Path to optional GeoTIFF (.tiff) export");
  app.add_option("--export-png-path", png_export_path, "Path to optional .png export");
  app.add_option("--geotiff-georef-method", geotiff_georef_method, "Georeferencing method for GeoTIFF export")
      ->transform(CLI::CheckedTransformer(georef_method_mapper, CLI::ignore_case));
  CLI11_PARSE(app, argc, argv)

  if (exists(qct_file_path)) {
    if (is_regular_file(qct_file_path)) {
      std::ifstream file{qct_file_path, std::ios::binary};
      try {
        const qct::QctFile qct_file = qct::QctFile::parse(qct_file_path, force_decode);
        qct::ex::GeoTiffExportOptions geotiff_export_options{geotiff_export_path, geotiff_georef_method};
        qct::ex::KmlExportOptions kml_export_options{kml_export_path};
        qct::ex::PngExportOptions png_export_options{png_export_path};
        exports(qct_file, geotiff_export_options, kml_export_options, png_export_options);
      } catch (const qct::QctException& e) {
        std::cerr << e.what() << std::endl;
      }
    } else {
      std::cerr << "Not a file: " << qct_file_path << std::endl;
    }
  } else {
    std::cerr << "File does not exist:  " << qct_file_path << std::endl;
  }
}

void exports(const qct::QctFile& qct_file, const qct::ex::GeoTiffExportOptions& geotiff_export_options,
             const qct::ex::KmlExportOptions& kml_export_options, const qct::ex::PngExportOptions& png_export_options) {
  std::vector<std::future<void>> export_futures{};
  if (!geotiff_export_options.path.empty()) {
    export_futures.push_back(std::async(std::launch::async, qct::ex::exportToFormat<qct::ex::GeoTiffExportOptions>,
                                        qct_file, geotiff_export_options));
  }
  if (!kml_export_options.path.empty()) {
    export_futures.push_back(std::async(std::launch::async, qct::ex::exportToFormat<qct::ex::KmlExportOptions>,
                                        qct_file, kml_export_options));
  }
  if (!png_export_options.path.empty()) {
    export_futures.push_back(std::async(std::launch::async, qct::ex::exportToFormat<qct::ex::PngExportOptions>,
                                        qct_file, png_export_options));
  }
  std::ranges::for_each(export_futures, [](auto& future) { future.wait(); });
}
