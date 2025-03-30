module;

#include <filesystem>

#include "stb_image_write.h"

export module qctexport:png;

import qct;

export namespace qct::ex::png {
/**
 *
 * @param png_export_path
 * @param qct_file
 */
void exportPng(const std::filesystem::path& png_export_path, const QctFile& qct_file) {
  stbi_write_png(png_export_path.string().c_str(), qct_file.width(), qct_file.height(), palette::COLOR_CHANNELS,
                 qct_file.image_index.bytes.data(), qct_file.width() * palette::COLOR_CHANNELS);
}

}  // namespace qct::ex::png
