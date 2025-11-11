module;

#include <cmath>
#include <filesystem>
#include <fstream>

export module qct:georef;

import :georef.coefficients;
import :georef.coordinates;
import :meta.datum;

export namespace qct::georef {
/**
 * Georeferencing information about the .QCT map file.
 */
struct Georef {
  GeorefCoefficients coefficients;

  /**
    * Convert given image coordinates into WGS-84 coordinates.
    * @param image_coordinates to convert
    * @param datum_shift to apply
    * @return WGS-84 coordinates
    */
  [[nodiscard]] Wgs84Coordinates toWgs84Coordinates(const ImageCoordinates& image_coordinates,
                                                    const meta::DatumShift& datum_shift) const;
  /**
   * Convert the given WGS-84 coordinates into image coordinates.
   * @param wgs84_coordinates to convert
   * @param datum_shift to apply
   * @return image coordinates
   */
  [[nodiscard]] ImageCoordinates toImageCoordinates(const Wgs84Coordinates& wgs84_coordinates,
                                                    const meta::DatumShift& datum_shift) const;

  static Georef parse(const std::filesystem::path& filepath);
};

Wgs84Coordinates Georef::toWgs84Coordinates(const ImageCoordinates& image_coordinates,
                                            const meta::DatumShift& datum_shift) const {
  // clang-format off
  double longitude =
      coefficients.lon_xxx * std::pow(image_coordinates.x, 3) +
      coefficients.lon_xx * std::pow(image_coordinates.x, 2) +
      coefficients.lon_x * image_coordinates.x +
      coefficients.lon_yyy * std::pow(image_coordinates.y, 3) +
      coefficients.lon_yy * std::pow(image_coordinates.y, 2) +
      coefficients.lon_y * image_coordinates.y +
      coefficients.lon_xxy * std::pow(image_coordinates.x, 2) * image_coordinates.y +
      coefficients.lon_xyy * image_coordinates.x * std::pow(image_coordinates.y, 2) +
      coefficients.lon_xy * image_coordinates.x * image_coordinates.y +
      coefficients.lon;
  double latitude =
      coefficients.lat_xxx * std::pow(image_coordinates.x, 3) +
      coefficients.lat_xx * std::pow(image_coordinates.x, 2) +
      coefficients.lat_x * image_coordinates.x +
      coefficients.lat_yyy * std::pow(image_coordinates.y, 3) +
      coefficients.lat_yy * std::pow(image_coordinates.y, 2) +
      coefficients.lat_y * image_coordinates.y +
      coefficients.lat_xxy * std::pow(image_coordinates.x, 2) * image_coordinates.y +
      coefficients.lat_xyy * image_coordinates.x * std::pow(image_coordinates.y, 2) +
      coefficients.lat_xy * image_coordinates.x * image_coordinates.y +
      coefficients.lat;
  // clang-format on
  longitude += datum_shift.east;
  latitude += datum_shift.north;
  return {.longitude = longitude, .latitude = latitude};
}

ImageCoordinates Georef::toImageCoordinates(const Wgs84Coordinates& wgs84_coordinates,
                                            const meta::DatumShift& datum_shift) const {
  const double longitude = wgs84_coordinates.longitude - datum_shift.east;
  const double latitude = wgs84_coordinates.latitude - datum_shift.north;

  // clang-format off
  const double x =
      coefficients.eas_xxx * std::pow(longitude, 3) +
      coefficients.eas_xx * std::pow(longitude, 2) +
      coefficients.eas_x * longitude +
      coefficients.eas_yyy * std::pow(latitude, 3) +
      coefficients.eas_yy * std::pow(latitude, 2) +
      coefficients.eas_y * latitude +
      coefficients.eas_yxx * latitude * std::pow(longitude, 2) +
      coefficients.eas_yyx * std::pow(latitude, 2) * longitude  +
      coefficients.eas_xy * longitude * latitude +
      coefficients.eas;
  const double y =
      coefficients.nor_xxx * std::pow(longitude, 3) +
      coefficients.nor_xx * std::pow(longitude, 2) +
      coefficients.nor_x * longitude +
      coefficients.nor_yyy * std::pow(latitude, 3) +
      coefficients.nor_yy * std::pow(latitude, 2) +
      coefficients.nor_y * latitude +
      coefficients.nor_yxx * latitude * std::pow(longitude, 2) +
      coefficients.nor_yyx * std::pow(latitude, 2) * longitude +
      coefficients.nor_xy * longitude * latitude +
      coefficients.nor;
  // clang-format on
  return {.x = x, .y = y};
}

Georef Georef::parse(const std::filesystem::path& filepath) {
  std::ifstream file{filepath, std::ios_base::binary};
  return {.coefficients = GeorefCoefficients::parse(filepath)};
}

}  // namespace qct::georef
