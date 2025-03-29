#include <filesystem>
#include <fstream>
#include <iostream>
#include <optional>

import qct;

int main(const int argc, char* argv[]) {
  if (argc < 2) {
    std::cerr << "Usage: " << argv[0] << " <path-to-qct-file>" << " " << "<path-to-kml-export-file>" << std::endl;
    return 1;
  }
  const std::filesystem::path qct_file_path{argv[1]};
  const std::filesystem::path kml_export_file_path{argv[2]};
  if (exists(qct_file_path)) {
    if (is_regular_file(qct_file_path)) {
      std::ifstream file{qct_file_path, std::ios::binary};
      std::cout << "Reading " << qct_file_path.string() << std::endl;
      qct::QctFile qct_file = qct::QctFile::parse(file, std::make_optional(kml_export_file_path));
    } else {
      std::cerr << "Not a file." << std::endl;
    }
  } else {
    std::cerr << "File does not exist:  " << qct_file_path << std::endl;
  }
}