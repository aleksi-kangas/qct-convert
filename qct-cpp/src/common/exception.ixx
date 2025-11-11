module;

#include <string>
#include <utility>

export module qct:common.exception;

export namespace qct {
struct QctException final : std::exception {
  explicit QctException(std::string message) : message_(std::move(message)) {}
  [[nodiscard]] const char* what() const noexcept override;

 private:
  std::string message_;
};

const char* QctException::what() const noexcept {
  return message_.c_str();
}
}  // namespace qct
