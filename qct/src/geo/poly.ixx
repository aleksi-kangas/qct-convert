module;

#include <cmath>

export module qct:geo.poly;

import :geo.coef;
import :meta.datum;

namespace qct::geo {
export struct Wgs84Coordinates final {
  double longitude;
  double latitude;
};

export struct ImageCoordinates final {
  double x;
  double y;
};

export Wgs84Coordinates toWgs84Coordinates(const ImageCoordinates& image_coordinates,
                                           const GeorefCoefficients& georef_coefficients,
                                           const meta::DatumShift& datum_shift);

export ImageCoordinates toImageCoordinates(const Wgs84Coordinates& wgs84_coordinates,
                                           const GeorefCoefficients& georef_coefficients,
                                           const meta::DatumShift& datum_shift);

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
  // clang-format off
  double x =
      georef_coefficients.eas_xxx * std::pow(wgs84_coordinates.longitude, 3) +
      georef_coefficients.eas_xx * std::pow(wgs84_coordinates.longitude, 2) +
      georef_coefficients.eas_x * wgs84_coordinates.longitude +
      georef_coefficients.eas_yyy * std::pow(wgs84_coordinates.latitude, 3) +
      georef_coefficients.eas_yy * std::pow(wgs84_coordinates.latitude, 2) +
      georef_coefficients.eas_y * wgs84_coordinates.latitude +
      georef_coefficients.eas_yxx * wgs84_coordinates.latitude * std::pow(wgs84_coordinates.longitude, 2) +
      georef_coefficients.eas_yyx * std::pow(wgs84_coordinates.latitude, 2) * wgs84_coordinates.longitude  +
      georef_coefficients.eas_xy * wgs84_coordinates.longitude * wgs84_coordinates.latitude +
      georef_coefficients.eas;
  double y =
      georef_coefficients.nor_xxx * std::pow(wgs84_coordinates.longitude, 3) +
      georef_coefficients.nor_xx * std::pow(wgs84_coordinates.longitude, 2) +
      georef_coefficients.nor_x * wgs84_coordinates.longitude +
      georef_coefficients.nor_yyy * std::pow(wgs84_coordinates.latitude, 3) +
      georef_coefficients.nor_yy * std::pow(wgs84_coordinates.latitude, 2) +
      georef_coefficients.nor_y * wgs84_coordinates.latitude +
      georef_coefficients.nor_yxx * wgs84_coordinates.latitude * std::pow(wgs84_coordinates.longitude, 2) +
      georef_coefficients.nor_yyx * std::pow(wgs84_coordinates.latitude, 2) * wgs84_coordinates.longitude +
      georef_coefficients.nor_xy * wgs84_coordinates.longitude * wgs84_coordinates.latitude +
      georef_coefficients.nor;
  // clang-format on
  x -= datum_shift.east;
  y -= datum_shift.north;
  return {.x = x, .y = y};
}

}  // namespace qct::geo
