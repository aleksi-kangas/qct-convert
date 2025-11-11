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

 protected:
  explicit ExportOptions(std::filesystem::path path)
      : path{std::move(path)} {}
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
    underlying().exportTo(qct_file, options);
  }

 private:
  friend C;

  [[nodiscard]] C& underlying() { return static_cast<C&>(*this); }
  [[nodiscard]] const C& underlying() const { return static_cast<const C&>(*this); }
};
}  // namespace qct::ex
