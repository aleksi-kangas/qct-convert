export module qct:georef.coordinates;

namespace qct::georef {
/**
 * WGS-84 coordinates.
 */
export struct Wgs84Coordinates final {
  double longitude{};
  double latitude{};
};

/**
 * Image coordinates.
 */
export struct ImageCoordinates final {
  double x{};
  double y{};
};
}  // namespace qct::georef
