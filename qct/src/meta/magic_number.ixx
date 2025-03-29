module;

#include <cstdint>
#include <string>

export module qct:meta.magic;

export namespace qct::meta {
/**
 *
 */
enum class MagicNumber : std::int32_t {
  QUICK_CHART_INFORMATION = 0x1423D5FE,
  QUICK_CHART_MAP = 0x1423D5FF
};

std::string to_string(const MagicNumber magic_number) {
  switch (magic_number) {
    case MagicNumber::QUICK_CHART_INFORMATION:
      return "Quick Chart Information";
    case MagicNumber::QUICK_CHART_MAP:
      return "Quick Chart Map";
    default:
      return "Unknown";
  }
}
}  // namespace qct::meta
