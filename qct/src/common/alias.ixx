module;

#include <cstdint>

export module qct:common.alias;

export namespace qct {
/**
 * A byte offset to the QCT-file.
 * A 64-bit offset for QC3 support. QCT technically requires only 32-bit offsets.
 */
using byte_offset_t = std::int64_t;
}  // namespace qct
