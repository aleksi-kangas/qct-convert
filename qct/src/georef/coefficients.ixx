module;

#include <cstdint>
#include <filesystem>
#include <fstream>

export module qct:georef.coef;

import :georef.coordinates;
import :meta.datum;
import :util.reader;

export namespace qct::georef {
/**
 * +--------+--------------+----------------------------------------------------+
 * | Offset | Size (Bytes) | Content                                            |
 * +--------+--------------+----------------------------------------------------+
 * | 0x0060 | 40 x 8       | Geographical Referencing Coefficients - 40 Doubles |
 * +--------+--------------+----------------------------------------------------+
 *
 * +------------+-------------------+-------------------+-------------------+-------------------+
 * | Offset     | 0x00              | 0x50              | 0xA0              | 0xF0              |
 * +------------+-------------------+-------------------+-------------------+-------------------+
 * | 0x00       | eas               | nor               | lat               | lon               |
 * | 0x08       | easY              | norY              | latX              | lonX              |
 * | 0x10       | easX              | norX              | latY              | lonY              |
 * | 0x18       | easYY             | norYY             | latXX             | lonXX             |
 * | 0x20       | easXY             | norXY             | latXY             | lonXY             |
 * | 0x28       | easXX             | norXX             | latYY             | lonYY             |
 * | 0x30       | easYYY            | norYYY            | latXXX            | lonXXX            |
 * | 0x38       | easYYX            | norYYX            | latXXY            | lonXXY            |
 * | 0x40       | easYXX            | norYXX            | latXYY            | lonXYY            |
 * | 0x48       | easXXX            | norXXX            | latYYY            | lonYYY            |
 * +------------+-------------------+-------------------+-------------------+-------------------+
 */
struct GeorefCoefficients final {
  static constexpr std::int32_t BYTE_OFFSET{0x0060};

  double eas;
  double eas_y;
  double eas_x;
  double eas_yy;
  double eas_xy;
  double eas_xx;
  double eas_yyy;
  double eas_yyx;
  double eas_yxx;
  double eas_xxx;

  double nor;
  double nor_y;
  double nor_x;
  double nor_yy;
  double nor_xy;
  double nor_xx;
  double nor_yyy;
  double nor_yyx;
  double nor_yxx;
  double nor_xxx;

  double lat;
  double lat_x;
  double lat_y;
  double lat_xx;
  double lat_xy;
  double lat_yy;
  double lat_xxx;
  double lat_xxy;
  double lat_xyy;
  double lat_yyy;

  double lon;
  double lon_x;
  double lon_y;
  double lon_xx;
  double lon_xy;
  double lon_yy;
  double lon_xxx;
  double lon_xxy;
  double lon_xyy;
  double lon_yyy;

  static GeorefCoefficients parse(const std::filesystem::path& filepath);

  friend std::ostream& operator<<(std::ostream& os, const GeorefCoefficients& georef_coefficients) {
    os << "Georef Coefficients:" << "\n"
       << "\t" << "Eas: [" << georef_coefficients.eas << ", " << georef_coefficients.eas_y << ", "
       << georef_coefficients.eas_x << ", " << georef_coefficients.eas_yy << ", " << georef_coefficients.eas_xy << ", "
       << georef_coefficients.eas_xx << ", " << georef_coefficients.eas_yyy << ", " << georef_coefficients.eas_yyx
       << ", " << georef_coefficients.eas_yxx << ", " << georef_coefficients.eas_xxx << "]\n";

    os << "\t" << "Nor: [" << georef_coefficients.nor << ", " << georef_coefficients.nor_y << ", "
       << georef_coefficients.nor_x << ", " << georef_coefficients.nor_yy << ", " << georef_coefficients.nor_xy << ", "
       << georef_coefficients.nor_xx << ", " << georef_coefficients.nor_yyy << ", " << georef_coefficients.nor_yyx
       << ", " << georef_coefficients.nor_yxx << ", " << georef_coefficients.nor_xxx << "]\n";

    os << "\t" << "Lat: [" << georef_coefficients.lat << ", " << georef_coefficients.lat_x << ", "
       << georef_coefficients.lat_y << ", " << georef_coefficients.lat_xx << ", " << georef_coefficients.lat_xy << ", "
       << georef_coefficients.lat_yy << ", " << georef_coefficients.lat_xxx << ", " << georef_coefficients.lat_xxy
       << ", " << georef_coefficients.lat_xyy << ", " << georef_coefficients.lat_yyy << "]\n";

    os << "\t" << "Lon: [" << georef_coefficients.lon << ", " << georef_coefficients.lon_x << ", "
       << georef_coefficients.lon_y << ", " << georef_coefficients.lon_xx << ", " << georef_coefficients.lon_xy << ", "
       << georef_coefficients.lon_yy << ", " << georef_coefficients.lon_xxx << ", " << georef_coefficients.lon_xxy
       << ", " << georef_coefficients.lon_xyy << ", " << georef_coefficients.lon_yyy << "]\n";
    return os;
  }
};
}  // namespace qct::georef

namespace qct::georef {
GeorefCoefficients GeorefCoefficients::parse(const std::filesystem::path& filepath) {
  std::ifstream file{filepath, std::ios::binary};
  const std::vector<double> eas_doubles = util::readDoubles(file, BYTE_OFFSET + 0x00, 10);
  const std::vector<double> nor_doubles = util::readDoubles(file, BYTE_OFFSET + 0x50, 10);
  const std::vector<double> lat_doubles = util::readDoubles(file, BYTE_OFFSET + 0xA0, 10);
  const std::vector<double> lon_doubles = util::readDoubles(file, BYTE_OFFSET + 0xF0, 10);
  // clang-format off
  return {
    .eas = eas_doubles[0],
    .eas_y = eas_doubles[1],
    .eas_x = eas_doubles[2],
    .eas_yy = eas_doubles[3],
    .eas_xy = eas_doubles[4],
    .eas_xx = eas_doubles[5],
    .eas_yyy = eas_doubles[6],
    .eas_yyx = eas_doubles[7],
    .eas_yxx = eas_doubles[8],
    .eas_xxx = eas_doubles[9],

    .nor = nor_doubles[0],
    .nor_y = nor_doubles[1],
    .nor_x = nor_doubles[2],
    .nor_yy = nor_doubles[3],
    .nor_xy = nor_doubles[4],
    .nor_xx = nor_doubles[5],
    .nor_yyy = nor_doubles[6],
    .nor_yyx = nor_doubles[7],
    .nor_yxx = nor_doubles[8],
    .nor_xxx = nor_doubles[9],

    .lat = lat_doubles[0],
    .lat_x = lat_doubles[1],
    .lat_y = lat_doubles[2],
    .lat_xx = lat_doubles[3],
    .lat_xy = lat_doubles[4],
    .lat_yy = lat_doubles[5],
    .lat_xxx = lat_doubles[6],
    .lat_xxy = lat_doubles[7],
    .lat_xyy = lat_doubles[8],
    .lat_yyy = lat_doubles[9],

    .lon = lon_doubles[0],
    .lon_x = lon_doubles[1],
    .lon_y = lon_doubles[2],
    .lon_xx = lon_doubles[3],
    .lon_xy = lon_doubles[4],
    .lon_yy = lon_doubles[5],
    .lon_xxx = lon_doubles[6],
    .lon_xxy = lon_doubles[7],
    .lon_xyy = lon_doubles[8],
    .lon_yyy = lon_doubles[9],
  };
  // clang-format on
}

}  // namespace qct::georef
