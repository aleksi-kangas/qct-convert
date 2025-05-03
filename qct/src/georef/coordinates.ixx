export module qct:georef.coordinates;

export namespace qct::georef {
/**
 * WGS-84 coordinates.
 */
struct Wgs84Coordinates final {
  double longitude;
  double latitude;
};

/**
 * Image coordinates.
 */
struct ImageCoordinates final {
  double x;
  double y;
};
}  // namespace qct::georef
