module;

#include <cmath>

export module qct:geo.poly;

import :geo.coef;
import :meta.datum;

export namespace qct::geo {
/**
 *
 */
struct Wgs84Coordinates final {
  double longitude;
  double latitude;
};

/**
 *
 */
struct ImageCoordinates final {
  double x;
  double y;
};

/**
 *
 * @param image_coordinates
 * @param georef_coefficients
 * @param datum_shift
 * @return
 */
Wgs84Coordinates toWgs84Coordinates(const ImageCoordinates& image_coordinates,
                                    const GeorefCoefficients& georef_coefficients, const meta::DatumShift& datum_shift);
/**
 *
 * @param wgs84_coordinates
 * @param georef_coefficients
 * @param datum_shift
 * @return
 */
ImageCoordinates toImageCoordinates(const Wgs84Coordinates& wgs84_coordinates,
                                    const GeorefCoefficients& georef_coefficients, const meta::DatumShift& datum_shift);
}  // namespace qct::geo

namespace qct::geo {
Wgs84Coordinates geo::toWgs84Coordinates(const ImageCoordinates& image_coordinates,
                                         const GeorefCoefficients& georef_coefficients,
                                         const meta::DatumShift& datum_shift) {
  // clang-format off
  double longitude =
      georef_coefficients.lon_xxx * std::pow(image_coordinates.x, 3) +
      georef_coefficients.lon_xx * std::pow(image_coordinates.x, 2) +
      georef_coefficients.lon_x * image_coordinates.x +
      georef_coefficients.lon_yyy * std::pow(image_coordinates.y, 3) +
      georef_coefficients.lon_yy * std::pow(image_coordinates.y, 2) +
      georef_coefficients.lon_y * image_coordinates.y +
      georef_coefficients.lon_xxy * std::pow(image_coordinates.x, 2) * image_coordinates.y +
      georef_coefficients.lon_xyy * image_coordinates.x * std::pow(image_coordinates.y, 2) +
      georef_coefficients.lon_xy * image_coordinates.x * image_coordinates.y +
      georef_coefficients.lon;
  double latitude =
      georef_coefficients.lat_xxx * std::pow(image_coordinates.x, 3) +
      georef_coefficients.lat_xx * std::pow(image_coordinates.x, 2) +
      georef_coefficients.lat_x * image_coordinates.x +
      georef_coefficients.lat_yyy * std::pow(image_coordinates.y, 3) +
      georef_coefficients.lat_yy * std::pow(image_coordinates.y, 2) +
      georef_coefficients.lat_y * image_coordinates.y +
      georef_coefficients.lat_xxy * std::pow(image_coordinates.x, 2) * image_coordinates.y +
      georef_coefficients.lat_xyy * image_coordinates.x * std::pow(image_coordinates.y, 2) +
      georef_coefficients.lat_xy * image_coordinates.x * image_coordinates.y +
      georef_coefficients.lat;
  // clang-format on
  longitude += datum_shift.east;
  latitude += datum_shift.north;
  return {.longitude = longitude, .latitude = latitude};
}

ImageCoordinates geo::toImageCoordinates(const Wgs84Coordinates& wgs84_coordinates,
                                         const GeorefCoefficients& georef_coefficients,
                                         const meta::DatumShift& datum_shift) {
  const double longitude = wgs84_coordinates.longitude - datum_shift.east;
  const double latitude = wgs84_coordinates.latitude - datum_shift.north;

  // clang-format off
  const double x =
      georef_coefficients.eas_xxx * std::pow(longitude, 3) +
      georef_coefficients.eas_xx * std::pow(longitude, 2) +
      georef_coefficients.eas_x * longitude +
      georef_coefficients.eas_yyy * std::pow(latitude, 3) +
      georef_coefficients.eas_yy * std::pow(latitude, 2) +
      georef_coefficients.eas_y * latitude +
      georef_coefficients.eas_yxx * latitude * std::pow(longitude, 2) +
      georef_coefficients.eas_yyx * std::pow(latitude, 2) * longitude  +
      georef_coefficients.eas_xy * longitude * latitude +
      georef_coefficients.eas;
  const double y =
      georef_coefficients.nor_xxx * std::pow(longitude, 3) +
      georef_coefficients.nor_xx * std::pow(longitude, 2) +
      georef_coefficients.nor_x * longitude +
      georef_coefficients.nor_yyy * std::pow(latitude, 3) +
      georef_coefficients.nor_yy * std::pow(latitude, 2) +
      georef_coefficients.nor_y * latitude +
      georef_coefficients.nor_yxx * latitude * std::pow(longitude, 2) +
      georef_coefficients.nor_yyx * std::pow(latitude, 2) * longitude +
      georef_coefficients.nor_xy * longitude * latitude +
      georef_coefficients.nor;
  // clang-format on
  return {.x = x, .y = y};
}

}  // namespace qct::geo
