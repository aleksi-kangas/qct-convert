#include <gtest/gtest.h>

import qct;

using namespace qct;

namespace {
class CoordinateTransformTest : public testing::Test {
 protected:
  geo::GeorefCoefficients georef_coefficients{};
  meta::DatumShift datum_shift{};

  void SetUp() override {
    georef_coefficients.eas = 200454;
    georef_coefficients.eas_y = -202.649;
    georef_coefficients.eas_x = 61806.4;
    georef_coefficients.eas_yy = -9.55793;
    georef_coefficients.eas_xy = -655.404;
    georef_coefficients.eas_xx = 14.0021;
    georef_coefficients.eas_yyy = 0;
    georef_coefficients.eas_yyx = 0;
    georef_coefficients.eas_yxx = 0;
    georef_coefficients.eas_xxx = 0;

    georef_coefficients.nor = 2.58091e+06;
    georef_coefficients.nor_y = -44396.1;
    georef_coefficients.nor_x = 2309.42;
    georef_coefficients.nor_yy = -3.56926;
    georef_coefficients.nor_xy = -52.2139;
    georef_coefficients.nor_xx = -175.788;
    georef_coefficients.nor_yyy = 0;
    georef_coefficients.nor_yyx = 0;
    georef_coefficients.nor_yxx = 0;
    georef_coefficients.nor_xxx = 0;

    georef_coefficients.lat = 57.7987;
    georef_coefficients.lat_x = 1.51247e-06;
    georef_coefficients.lat_y = -2.23875e-05;
    georef_coefficients.lat_xx = -6.90782e-12;
    georef_coefficients.lat_xy = -6.5338e-13;
    georef_coefficients.lat_yy = -4.9429e-14;
    georef_coefficients.lat_xxx = 0;
    georef_coefficients.lat_xxy = 0;
    georef_coefficients.lat_xyy = 0;
    georef_coefficients.lat_yyy = 0;

    georef_coefficients.lon = -6.57964;
    georef_coefficients.lon_x = 4.19306e-05;
    georef_coefficients.lon_y = 2.83193e-06;
    georef_coefficients.lon_xx = 1.43079e-12;
    georef_coefficients.lon_xy = -2.56497e-11;
    georef_coefficients.lon_yy = -1.43062e-12;
    georef_coefficients.lon_xxx = 0;
    georef_coefficients.lon_xxy = 0;
    georef_coefficients.lon_xyy = 0;
    georef_coefficients.lon_yyy = 0;

    datum_shift.east = 10.0;
    datum_shift.north = 20.0;
  }
};
}  // namespace

TEST_F(CoordinateTransformTest, FromImageToWgs84) {
  constexpr geo::ImageCoordinates img{.x = 10.0, .y = 20.0};
  const auto [longitude, latitude] = toWgs84Coordinates(img, georef_coefficients, datum_shift);

  double expected_lon = georef_coefficients.lon_xxx * (img.x * img.x * img.x) +  // lonXXX * x³
                        georef_coefficients.lon_xx * (img.x * img.x) +           // lonXX * x²
                        georef_coefficients.lon_x * img.x +                      // lonX * x
                        georef_coefficients.lon_yyy * (img.y * img.y * img.y) +  // lonYYY * y³
                        georef_coefficients.lon_y * (img.y * img.y) +            // lonYY * y²
                        georef_coefficients.lon_y * img.y +                      // lonY * y
                        georef_coefficients.lon_xxy * (img.x * img.x) * img.y +  // lonXXY * x²y
                        georef_coefficients.lon_xyy * img.x * (img.y * img.y) +  // lonXYY * xy²
                        georef_coefficients.lon_xy * img.x * img.y +             // lonXY * xy
                        georef_coefficients.lon;                                 // lon
  expected_lon += datum_shift.east;

  double expected_lat = georef_coefficients.lat_xxx * (img.x * img.x * img.x) +  // latXXX * x³
                        georef_coefficients.lat_xx * (img.x * img.x) +           // latXX * x²
                        georef_coefficients.lat_x * img.x +                      // latX * x
                        georef_coefficients.lat_yyy * (img.y * img.y * img.y) +  // latYYY * y³
                        georef_coefficients.lat_yy * (img.y * img.y) +           // latYY * y²
                        georef_coefficients.lat_y * img.y +                      // latY * y
                        georef_coefficients.lat_xxy * (img.x * img.x) * img.y +  // latXXY * x²y
                        georef_coefficients.lat_xyy * img.x * (img.y * img.y) +  // latXYY * xy²
                        georef_coefficients.lat_xy * img.x * img.y +             // latXY * xy
                        georef_coefficients.lat;                                 // lat
  expected_lat += datum_shift.north;

  EXPECT_NEAR(longitude, expected_lon, 1e-2);
  EXPECT_NEAR(latitude, expected_lat, 1e-2);
}

TEST_F(CoordinateTransformTest, FromWgs84ToImage) {
  constexpr geo::Wgs84Coordinates wgs84{.longitude = 30.0, .latitude = 40.0};
  const auto [x, y] = toImageCoordinates(wgs84, georef_coefficients, datum_shift);

  const double lon = wgs84.longitude - datum_shift.east;
  const double lat = wgs84.latitude - datum_shift.north;

  const double expected_x = georef_coefficients.eas_xxx * (lon * lon * lon) +  // xxx * λ³
                            georef_coefficients.eas_xx * (lon * lon) +         // xx * λ²
                            georef_coefficients.eas_x * lon +                  // x * λ
                            georef_coefficients.eas_yyy * (lat * lat * lat) +  // yyy * φ³
                            georef_coefficients.eas_yy * (lat * lat) +         // yy * φ²
                            georef_coefficients.eas_y * lat +                  // y * φ
                            georef_coefficients.eas_yxx * (lon * lon) * lat +  // yxx * λ²φ
                            georef_coefficients.eas_yyx * (lat * lat) * lon +  // yyx * φ²λ
                            georef_coefficients.eas_xy * lon * lat +           // xy * λφ
                            georef_coefficients.eas;                           // eas

  const double expected_y = georef_coefficients.nor_xxx * (lon * lon * lon) +  // xxx * λ³
                            georef_coefficients.nor_xx * (lon * lon) +         // xx * λ²
                            georef_coefficients.nor_x * lon +                  // x * λ
                            georef_coefficients.nor_yyy * (lat * lat * lat) +  // yyy * φ³
                            georef_coefficients.nor_yy * (lat * lat) +         // yy * φ²
                            georef_coefficients.nor_y * lat +                  // y * φ
                            georef_coefficients.nor_yxx * (lon * lon) * lat +  // yxx * λ²φ
                            georef_coefficients.nor_yyx * (lat * lat) * lon +  // yyx * φ²λ
                            georef_coefficients.nor_xy * lon * lat +           // xy * λφ
                            georef_coefficients.nor;

  EXPECT_NEAR(x, expected_x, 1e-2);
  EXPECT_NEAR(y, expected_y, 1e-2);
}

TEST_F(CoordinateTransformTest, RoundTrip_Consistency) {
  constexpr geo::ImageCoordinates image_coordinates{.x = 50.0, .y = 60.0};
  const auto wgs84_coordinates = toWgs84Coordinates(image_coordinates, georef_coefficients, datum_shift);
  const auto [x, y] = toImageCoordinates(wgs84_coordinates, georef_coefficients, datum_shift);

  EXPECT_NEAR(x, image_coordinates.x, 1e-0);
  EXPECT_NEAR(y, image_coordinates.y, 1e-0);
}
