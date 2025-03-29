module;

#include <cstdint>
#include <fstream>
#include <vector>

export module qct:meta.outline;

import :util.reader;

export namespace qct::meta {
/**
 *
 */
struct MapOutline final {
  struct Point final {
    double latitude{0};
    double longitude{0};

    static Point parse(std::ifstream& file, std::int32_t byteOffset);

    friend std::ostream& operator<<(std::ostream& os, const Point& point) {
      os << "Lat: " << point.latitude << ", Lon: " << point.longitude;
      return os;
    }
  };
  std::vector<Point> points{};

  static MapOutline parse(std::ifstream& file, std::int32_t pointCountByteOffset, std::int32_t arrayPointerByteOffset);

  friend std::ostream& operator<<(std::ostream& os, const MapOutline& map_outline) {
    os << "\n";
    for (const auto& point : map_outline.points) {
      os << "\t\t" << point << "\n";
    }
    return os;
  }
};
}  // namespace qct::meta

namespace qct::meta {
MapOutline::Point MapOutline::Point::parse(std::ifstream& file, const std::int32_t byteOffset) {
  return {util::readDouble(file, byteOffset + 0x00), util::readDouble(file, byteOffset + 0x08)};
}

MapOutline MapOutline::parse(std::ifstream& file, const std::int32_t pointCountByteOffset,
                             const std::int32_t arrayPointerByteOffset) {
  const std::int32_t pointCount = util::readInt(file, pointCountByteOffset);
  const std::int32_t arrayByteOffset = util::readInt(file, arrayPointerByteOffset);
  std::vector<Point> points(pointCount);
  for (std::int32_t i = 0; i < pointCount; ++i) {
    points[i] = Point::parse(file, arrayByteOffset + i * (0x08 + 0x08));
  }
  return {points};
}

}  // namespace qct::meta
