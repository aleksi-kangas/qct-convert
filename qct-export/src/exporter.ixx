module;

#include <filesystem>
#include <format>
#include <iostream>
#include <utility>

export module qctexport:exporter;

import qct;

import :exception;

export namespace qct::ex {
/**
 * The base export options.
 */
struct ExportOptions {
  virtual ~ExportOptions() = default;
  std::filesystem::path path{};
  bool overwrite{true};

 protected:
  explicit ExportOptions(std::filesystem::path path, const bool overwrite = true)
      : path{std::move(path)}, overwrite{overwrite} {}
};

/**
 * A base class for QCT exporters.
 * @tparam O the export options type
 */
template <class C, typename O>
class AbstractExporter {
 public:
  virtual ~AbstractExporter() = default;

  void exportTo(const QctFile& qct_file, const O& options) const {
    checkOverwrite(options);
    underlying().exportTo(qct_file, options);
  }

 protected:
  static void checkOverwrite(const O& options) {
    if (!options.overwrite && std::filesystem::exists(options.path)) {
      throw QctExportException{
          std::format("File {} already exists, and overwrite is disabled.", options.path.string())};
    }
  }

 private:
  friend C;

  [[nodiscard]] C& underlying() { return static_cast<C&>(*this); }
  [[nodiscard]] const C& underlying() const { return static_cast<const C&>(*this); }
};
}  // namespace qct::ex
