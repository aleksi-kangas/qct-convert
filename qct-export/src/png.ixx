module;

#include <filesystem>
#include <mutex>

#include "fpng.h"

export module qctexport:png;

import qct;

import :exception;
import :exporter;

export namespace qct::ex {
/**
 * Options for exporting a QCT file to a PNG file.
 */
struct PngExportOptions final : ExportOptions {
  explicit PngExportOptions(const std::filesystem::path& path) : ExportOptions{path} {}
};

/**
 * Exporter for PNG files.
 */
class PngExporter final : public AbstractExporter<PngExporter, PngExportOptions> {
 public:
  PngExporter();
  ~PngExporter() override = default;

  /**
   * Export the given QCT file to the specified path as a PNG file.
   *
   * @param qct_file The QCT file to export.
   * @param options The export options for the PNG export.
   */
  void exportTo(const QctFile& qct_file, const PngExportOptions& options) const;

 private:
  static std::once_flag once_flag_;
};

std::once_flag PngExporter::once_flag_{};

PngExporter::PngExporter() {
  std::call_once(once_flag_, fpng::fpng_init);
}

void PngExporter::exportTo(const QctFile& qct_file, const PngExportOptions& options) const {
  if (!fpng::fpng_encode_image_to_file(options.path.string().c_str(), qct_file.image_index.imageBytesView().data(),
                                       qct_file.width(), qct_file.height(), palette::COLOR_CHANNELS, 0)) {
    throw QctExportException{"Failed to export PNG file."};
  }
}

}  // namespace qct::ex
