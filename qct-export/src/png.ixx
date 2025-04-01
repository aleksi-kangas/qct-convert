module;

#include <filesystem>
#include <iostream>
#include <mutex>

#include "fpng.h"

export module qctexport:png;

import qct;

namespace qct::ex::png {
/**
 *
 * @param png_export_path
 * @param qct_file
 */
export void exportPng(const std::filesystem::path& png_export_path, const QctFile& qct_file);
}  // namespace qct::ex::png

namespace qct::ex::png {
static std::once_flag once{};

void exportPng(const std::filesystem::path& png_export_path, const QctFile& qct_file) {
  std::call_once(once, fpng::fpng_init);
  if (!fpng::fpng_encode_image_to_file(png_export_path.string().c_str(), qct_file.image_index.bytes.data(),
                                       qct_file.width(), qct_file.height(), palette::COLOR_CHANNELS, 0)) {
    std::cerr << "Failed to export .png" << std::endl;
  }
}
}  // namespace qct::ex::png
