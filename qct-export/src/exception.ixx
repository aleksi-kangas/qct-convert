module;

#include <string>
#include <utility>

export module qctexport:exception;

export namespace qct::ex {
struct QctExportException final : std::exception {
  explicit QctExportException(std::string message) : message_(std::move(message)) {}
  [[nodiscard]] const char* what() const override;

 private:
  std::string message_;
};

const char* QctExportException::what() const {
  return message_.c_str();
}
}  // namespace qct::ex
