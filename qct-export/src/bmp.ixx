module;

#include <filesystem>

#include "stb_image_write.h"

export module qctexport:bmp;

import qct;

export namespace qct::ex::bmp {
/**
 *
 * @param bmp_export_path
 * @param qct_file
 */
void exportBmp(const std::filesystem::path& bmp_export_path, const QctFile& qct_file) {
  stbi_write_bmp(bmp_export_path.string().c_str(), qct_file.width(), qct_file.height(), palette::COLOR_CHANNELS,
                 qct_file.image_index.bytes.data());
}

}  // namespace qct::ex::bmp
