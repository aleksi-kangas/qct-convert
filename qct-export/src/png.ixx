module;

#include <filesystem>
#include <mutex>

#include "fpng.h"

export module qctexport:png;

import qct;
import :exception;

namespace qct::ex::png {
/**
 * Export a PNG file from a QCT file.
 * @param png_export_path the path to the PNG file to export
 * @param qct_file the QCT file to export
 */
export void exportPng(const std::filesystem::path& png_export_path, const QctFile& qct_file);

static std::once_flag once{};

void exportPng(const std::filesystem::path& png_export_path, const QctFile& qct_file) {
  std::call_once(once, fpng::fpng_init);
  if (!fpng::fpng_encode_image_to_file(png_export_path.string().c_str(), qct_file.image_index.imageBytesView().data(),
                                       qct_file.width(), qct_file.height(), palette::COLOR_CHANNELS, 0)) {
    throw QctExportException{"Failed to export PNG file."};
  }
}

}  // namespace qct::ex::png
