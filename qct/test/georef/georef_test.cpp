#include <filesystem>
#include <fstream>
#include <vector>

#include <gtest/gtest.h>

import qct;

using namespace qct;

class GeorefTest : public testing::Test {
 protected:
  std::filesystem::path temporary_file_path{std::filesystem::temp_directory_path() / "test.qct"};

  georef::Georef georef{};
  meta::DatumShift datum_shift{};

  void SetUp() override {}

  void TearDown() override {
    if (std::filesystem::exists(temporary_file_path)) {
      std::filesystem::remove(temporary_file_path);
    }
  }

  static void createTestBinaryFile(const std::filesystem::path& path, const std::vector<double>& eas,
                                   const std::vector<double>& nor, const std::vector<double>& lat,
                                   const std::vector<double>& lon);
};

TEST_F(GeorefTest, ParseGeorefCoefficients) {
  const std::vector eas{1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
  const std::vector nor{11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0};
  const std::vector lat{21.0, 22.0, 23.0, 24.0, 25.0, 26.0, 27.0, 28.0, 29.0, 30.0};
  const std::vector lon{31.0, 32.0, 33.0, 34.0, 35.0, 36.0, 37.0, 38.0, 39.0, 40.0};
  createTestBinaryFile(temporary_file_path, eas, nor, lat, lon);

  const georef::GeorefCoefficients coefficients = georef::GeorefCoefficients::parse(temporary_file_path);

  EXPECT_DOUBLE_EQ(coefficients.eas, 1.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_y, 2.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_x, 3.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_yy, 4.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_xy, 5.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_xx, 6.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_yyy, 7.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_yyx, 8.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_yxx, 9.0);
  EXPECT_DOUBLE_EQ(coefficients.eas_xxx, 10.0);

  EXPECT_DOUBLE_EQ(coefficients.nor, 11.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_y, 12.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_x, 13.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_yy, 14.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_xy, 15.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_xx, 16.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_yyy, 17.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_yyx, 18.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_yxx, 19.0);
  EXPECT_DOUBLE_EQ(coefficients.nor_xxx, 20.0);

  EXPECT_DOUBLE_EQ(coefficients.lat, 21.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_x, 22.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_y, 23.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_xx, 24.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_xy, 25.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_yy, 26.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_xxx, 27.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_xxy, 28.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_xyy, 29.0);
  EXPECT_DOUBLE_EQ(coefficients.lat_yyy, 30.0);

  EXPECT_DOUBLE_EQ(coefficients.lon, 31.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_x, 32.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_y, 33.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_xx, 34.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_xy, 35.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_yy, 36.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_xxx, 37.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_xxy, 38.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_xyy, 39.0);
  EXPECT_DOUBLE_EQ(coefficients.lon_yyy, 40.0);
}

TEST_F(GeorefTest, ToWgs84Coordinates) {
  constexpr georef::GeorefCoefficients coefficients{
      .lat = 20.0, .lat_x = 4.0, .lat_y = 5.0, .lon = 10.0, .lon_x = 2.0, .lon_y = 3.0};
  constexpr georef::Georef georef{.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift{.north = 2.0, .east = 1.0};
  constexpr georef::ImageCoordinates image_coordinates{.x = 2.0, .y = 3.0};

  const auto [longitude, latitude] = georef.toWgs84Coordinates(image_coordinates, datum_shift);
  // longitude = lon + lon_x * x + lon_y * y + east
  //           = 10.0 + 2.0 * 2.0 + 3.0 * 3.0 + 1.0 = 10.0 + 4.0 + 9.0 + 1.0 = 24.0
  EXPECT_DOUBLE_EQ(longitude, 24.0);
  // latitude = lat + lat_x * x + lat_y * y + north
  //          = 20.0 + 4.0 * 2.0 + 5.0 * 3.0 + 2.0 = 20.0 + 8.0 + 15.0 + 2.0 = 45.0
  EXPECT_DOUBLE_EQ(latitude, 45.0);
}

TEST_F(GeorefTest, ToImageCoordinates) {
  constexpr georef::GeorefCoefficients coefficients{
      .eas = 5.0, .eas_y = 2.0, .eas_x = 1.0, .nor = 10.0, .nor_y = 4.0, .nor_x = 3.0};
  constexpr georef::Georef georef{.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift{.north = 2.0, .east = 1.0};
  constexpr georef::Wgs84Coordinates wgs84_coordinates{.longitude = 3.0, .latitude = 4.0};

  const auto [x, y] = georef.toImageCoordinates(wgs84_coordinates, datum_shift);
  // Adjusted coordinates: longitude = 3.0 - 1.0 = 2.0, latitude = 4.0 - 2.0 = 2.0
  // x = eas + eas_x * longitude + eas_y * latitude
  //   = 5.0 + 1.0 * 2.0 + 2.0 * 2.0 = 5.0 + 2.0 + 4.0 = 11.0
  EXPECT_DOUBLE_EQ(x, 11.0);
  // y = nor + nor_x * longitude + nor_y * latitude
  //   = 10.0 + 3.0 * 2.0 + 4.0 * 2.0 = 10.0 + 6.0 + 8.0 = 24.0
  EXPECT_DOUBLE_EQ(y, 24.0);
}

TEST_F(GeorefTest, RoundTripConversion) {
  constexpr georef::GeorefCoefficients coefficients{.eas = 0.0,
                                                    .eas_y = 0.0,
                                                    .eas_x = 1.0,
                                                    .nor = 0.0,
                                                    .nor_y = 1.0,
                                                    .nor_x = 0.0,
                                                    .lat = 0.0,
                                                    .lat_x = 0.0,
                                                    .lat_y = 1.0,
                                                    .lon = 0.0,
                                                    .lon_x = 1.0,
                                                    .lon_y = 0.0};
  constexpr georef::Georef georef{.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift{.north = 0.0, .east = 0.0};
  constexpr georef::ImageCoordinates image_coordinates{.x = 10.0, .y = 20.0};

  const georef::Wgs84Coordinates wgs84_coordinates = georef.toWgs84Coordinates(image_coordinates, datum_shift);
  EXPECT_DOUBLE_EQ(wgs84_coordinates.longitude, 10.0);
  EXPECT_DOUBLE_EQ(wgs84_coordinates.latitude, 20.0);

  const auto [x, y] = georef.toImageCoordinates(wgs84_coordinates, datum_shift);
  EXPECT_DOUBLE_EQ(x, 10.0);
  EXPECT_DOUBLE_EQ(y, 20.0);
}

TEST_F(GeorefTest, ToWgs84CoordinatesSecondOrder) {
  constexpr georef::GeorefCoefficients coefficients{
      .lat_xx = 5.0, .lat_xy = 6.0, .lat_yy = 7.0, .lon_xx = 2.0, .lon_xy = 3.0, .lon_yy = 4.0};
  constexpr georef::Georef georef{.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift{.north = 2.0, .east = 1.0};
  constexpr georef::ImageCoordinates image_coordinates{.x = 2.0, .y = 3.0};

  const auto [longitude, latitude] = georef.toWgs84Coordinates(image_coordinates, datum_shift);
  // longitude = lon_xx * x^2 + lon_xy * x * y + lon_yy * y^2 + east
  //           = 2.0 * (2.0)^2 + 3.0 * 2.0 * 3.0 + 4.0 * (3.0)^2 + 1.0
  //           = 2.0 * 4.0 + 3.0 * 6.0 + 4.0 * 9.0 + 1.0
  //           = 8.0 + 18.0 + 36.0 + 1.0 = 63.0
  EXPECT_DOUBLE_EQ(longitude, 63.0);
  // latitude = lat_xx * x^2 + lat_xy * x * y + lat_yy * y^2 + north
  //          = 5.0 * (2.0)^2 + 6.0 * 2.0 * 3.0 + 7.0 * (3.0)^2 + 2.0
  //          = 5.0 * 4.0 + 6.0 * 6.0 + 7.0 * 9.0 + 2.0
  //          = 20.0 + 36.0 + 63.0 + 2.0 = 121.0
  EXPECT_DOUBLE_EQ(latitude, 121.0);
}

TEST_F(GeorefTest, ToImageCoordinatesSecondOrder) {
  constexpr georef::GeorefCoefficients coefficients{
      .eas_yy = 3.0, .eas_xy = 2.0, .eas_xx = 1.0, .nor_yy = 6.0, .nor_xy = 5.0, .nor_xx = 4.0};
  constexpr georef::Georef georef{.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift{.north = 2.0, .east = 1.0};
  constexpr georef::Wgs84Coordinates wgs84_coordinates{.longitude = 3.0, .latitude = 4.0};

  const auto [x, y] = georef.toImageCoordinates(wgs84_coordinates, datum_shift);
  // Adjusted coordinates: longitude = 3.0 - 1.0 = 2.0, latitude = 4.0 - 2.0 = 2.0
  // x = eas_xx * longitude^2 + eas_xy * longitude * latitude + eas_yy * latitude^2
  //   = 1.0 * (2.0)^2 + 2.0 * 2.0 * 2.0 + 3.0 * (2.0)^2
  //   = 1.0 * 4.0 + 2.0 * 4.0 + 3.0 * 4.0
  //   = 4.0 + 8.0 + 12.0 = 24.0
  EXPECT_DOUBLE_EQ(x, 24.0);
  // y = nor_xx * longitude^2 + nor_xy * longitude * latitude + nor_yy * latitude^2
  //   = 4.0 * (2.0)^2 + 5.0 * 2.0 * 2.0 + 6.0 * (2.0)^2
  //   = 4.0 * 4.0 + 5.0 * 4.0 + 6.0 * 4.0
  //   = 16.0 + 20.0 + 24.0 = 60.0
  EXPECT_DOUBLE_EQ(y, 60.0);
}

TEST_F(GeorefTest, RoundTripConversionSecondOrder) {
  constexpr georef::GeorefCoefficients coefficients{.eas_xx = 1.0, .nor_yy = 1.0, .lat_yy = 1.0, .lon_xx = 1.0};
  constexpr georef::Georef georef = {.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift = {.north = 0.0, .east = 0.0};
  constexpr georef::ImageCoordinates image_coordinates = {.x = 2.0, .y = 3.0};

  const georef::Wgs84Coordinates wgs84_coordinates = georef.toWgs84Coordinates(image_coordinates, datum_shift);
  // longitude = lon_xx * x^2 = 1.0 * (2.0)^2 = 4.0
  EXPECT_DOUBLE_EQ(wgs84_coordinates.longitude, 4.0);
  // latitude = lat_yy * y^2 = 1.0 * (3.0)^2 = 9.0
  EXPECT_DOUBLE_EQ(wgs84_coordinates.latitude, 9.0);

  const auto [x, y] = georef.toImageCoordinates(wgs84_coordinates, datum_shift);
  // x = eas_xx * longitude^2 = 1.0 * (4.0)^2 = 16.0
  EXPECT_DOUBLE_EQ(x, 16.0);
  // y = nor_yy * latitude^2 = 1.0 * (9.0)^2 = 81.0
  EXPECT_DOUBLE_EQ(y, 81.0);
}

TEST_F(GeorefTest, CombinedCoefficientsUpToSecondOrder) {
  georef::GeorefCoefficients coefficients{};
  // Constant terms
  coefficients.eas = 1.0;
  coefficients.nor = 2.0;
  coefficients.lat = 3.0;
  coefficients.lon = 4.0;
  // Linear terms
  coefficients.eas_x = 5.0;
  coefficients.eas_y = 6.0;
  coefficients.nor_x = 7.0;
  coefficients.nor_y = 8.0;
  coefficients.lat_x = 9.0;
  coefficients.lat_y = 10.0;
  coefficients.lon_x = 11.0;
  coefficients.lon_y = 12.0;
  // Second-order terms
  coefficients.eas_xx = 13.0;
  coefficients.eas_xy = 14.0;
  coefficients.eas_yy = 15.0;
  coefficients.nor_xx = 16.0;
  coefficients.nor_xy = 17.0;
  coefficients.nor_yy = 18.0;
  coefficients.lat_xx = 19.0;
  coefficients.lat_xy = 20.0;
  coefficients.lat_yy = 21.0;
  coefficients.lon_xx = 22.0;
  coefficients.lon_xy = 23.0;
  coefficients.lon_yy = 24.0;
  const georef::Georef georef{.coefficients = coefficients};
  constexpr meta::DatumShift datum_shift{.north = 2.0, .east = 1.0};
  constexpr georef::ImageCoordinates image_coordinates = {.x = 2.0, .y = 3.0};

  const auto [longitude, latitude] = georef.toWgs84Coordinates(image_coordinates, datum_shift);
  // longitude = lon + lon_x * x + lon_y * y + lon_xx * x^2 + lon_xy * x * y + lon_yy * y^2 + east
  //           = 4.0 + 11.0 * 2.0 + 12.0 * 3.0 + 22.0 * (2.0)^2 + 23.0 * 2.0 * 3.0 + 24.0 * (3.0)^2 + 1.0
  //           = 4.0 + 22.0 + 36.0 + 22.0 * 4.0 + 23.0 * 6.0 + 24.0 * 9.0 + 1.0
  //           = 4.0 + 22.0 + 36.0 + 88.0 + 138.0 + 216.0 + 1.0 = 505.0
  EXPECT_DOUBLE_EQ(longitude, 505.0);
  // latitude = lat + lat_x * x + lat_y * y + lat_xx * x^2 + lat_xy * x * y + lat_yy * y^2 + north
  //          = 3.0 + 9.0 * 2.0 + 10.0 * 3.0 + 19.0 * (2.0)^2 + 20.0 * 2.0 * 3.0 + 21.0 * (3.0)^2 + 2.0
  //          = 3.0 + 18.0 + 30.0 + 19.0 * 4.0 + 20.0 * 6.0 + 21.0 * 9.0 + 2.0
  //          = 3.0 + 18.0 + 30.0 + 76.0 + 120.0 + 189.0 + 2.0 = 438.0
  EXPECT_DOUBLE_EQ(latitude, 438.0);

  constexpr georef::Wgs84Coordinates wgs84_coordinates = {.longitude = 3.0, .latitude = 4.0};

  const auto [x, y] = georef.toImageCoordinates(wgs84_coordinates, datum_shift);
  // Adjusted coordinates: longitude = 3.0 - 1.0 = 2.0, latitude = 4.0 - 2.0 = 2.0
  // x = eas + eas_x * longitude + eas_y * latitude + eas_xx * longitude^2 + eas_xy * longitude * latitude + eas_yy * latitude^2
  //   = 1.0 + 5.0 * 2.0 + 6.0 * 2.0 + 13.0 * (2.0)^2 + 14.0 * 2.0 * 2.0 + 15.0 * (2.0)^2
  //   = 1.0 + 10.0 + 12.0 + 13.0 * 4.0 + 14.0 * 4.0 + 15.0 * 4.0
  //   = 1.0 + 10.0 + 12.0 + 52.0 + 56.0 + 60.0 = 191.0
  EXPECT_DOUBLE_EQ(x, 191.0);
  // y = nor + nor_x * longitude + nor_y * latitude + nor_xx * longitude^2 + nor_xy * longitude * latitude + nor_yy * latitude^2
  //   = 2.0 + 7.0 * 2.0 + 8.0 * 2.0 + 16.0 * (2.0)^2 + 17.0 * 2.0 * 2.0 + 18.0 * (2.0)^2
  //   = 2.0 + 14.0 + 16.0 + 16.0 * 4.0 + 17.0 * 4.0 + 18.0 * 4.0
  //   = 2.0 + 14.0 + 16.0 + 64.0 + 68.0 + 72.0 = 236.0
  EXPECT_DOUBLE_EQ(y, 236.0);
}

void GeorefTest::createTestBinaryFile(const std::filesystem::path& path, const std::vector<double>& eas,
                                      const std::vector<double>& nor, const std::vector<double>& lat,
                                      const std::vector<double>& lon) {
  std::ofstream file{path, std::ios::binary};
  const std::vector<char> padding(georef::GeorefCoefficients::BYTE_OFFSET, 0);
  file.write(padding.data(), padding.size());

  file.write(reinterpret_cast<const char*>(eas.data()), eas.size() * sizeof(double));
  file.seekp(96 + 0x50, std::ios::beg);
  file.write(reinterpret_cast<const char*>(nor.data()), nor.size() * sizeof(double));
  file.seekp(96 + 0xA0, std::ios::beg);
  file.write(reinterpret_cast<const char*>(lat.data()), lat.size() * sizeof(double));
  file.seekp(96 + 0xF0, std::ios::beg);
  file.write(reinterpret_cast<const char*>(lon.data()), lon.size() * sizeof(double));
}
