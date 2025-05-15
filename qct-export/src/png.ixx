module;

#include <filesystem>
#include <mutex>

#include "fpng.h"

export module qctexport:png;

import qct;

import :exception;
import :exporter;

namespace qct::ex {
class PngExporter final : public AbstractExporter<PngExporter> {
 public:
  PngExporter();

  /**
   * Export the given QCT file to the specified path as a PNG file.
   *
   * @param qct_file The QCT file to export.
   * @param path The path where the exported PNG file should be saved.
   */
  void exportTo(const QctFile& qct_file, const std::filesystem::path& path) const;

 private:
  static std::once_flag once_flag_;
};

std::once_flag PngExporter::once_flag_{};

PngExporter::PngExporter() {
  std::call_once(once_flag_, fpng::fpng_init);
}

void PngExporter::exportTo(const QctFile& qct_file, const std::filesystem::path& path) const {
  if (!fpng::fpng_encode_image_to_file(path.string().c_str(), qct_file.image_index.imageBytesView().data(),
                                       qct_file.width(), qct_file.height(), palette::COLOR_CHANNELS, 0)) {
    throw QctExportException{"Failed to export PNG file."};
  }
}

}  // namespace qct::ex
