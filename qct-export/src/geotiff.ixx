module;

#include <array>
#include <cstdint>
#include <filesystem>
#include <format>
#include <iostream>
#include <mutex>
#include <stdexcept>
#include <vector>

#include "gdal_priv.h"
#include "gdalwarper.h"
#include "proj.h"

export module qctexport:geotiff;

import qct;

import :exception;
import :exporter;

export namespace qct::ex {
/**
 * Options for exporting a QCT file to a GeoTIFF file.
 */
struct GeoTiffExportOptions final : ExportOptions {
  enum class GeorefMethod {
    /**
     * Automatically try to determine the better georeferencing method (GCP vs Linear Affine).
     */
    AUTOMATIC,
    /**
     * Georeferencing using GCPs (Ground Control Points).
     * Recommended when:
     * - The QCT-file contains non-zero higher order georeferencing coefficients.
     * Note:
     * - Not all GIS software can handle GCPs.
     */
    GCP,
    /**
     * Georeferencing using the constant and first order georeferencing coefficients from the QCT-file.
     * Recommended when:
     * - The QCT-file contains only non-zero constant and first order georeferencing coefficients.
     * Note:
     * - If the QCT-file contains non-zero higher order georeferencing coefficients, the georeferencing method will not be accurate.
     * - Accuracy depends on the magnitude of the higher order coefficients.
     */
    LINEAR
  };
  GeorefMethod georef_method{GeorefMethod::AUTOMATIC};

  explicit GeoTiffExportOptions(const std::filesystem::path& path, const bool overwrite,
                                const GeorefMethod georef_method)
      : ExportOptions{path, overwrite}, georef_method{georef_method} {}
};

/**
 * Exporter for GeoTIFF files.
 */
class GeoTiffExporter final : public QctExporter<GeoTiffExportOptions> {
 public:
  GeoTiffExporter();
  ~GeoTiffExporter() override = default;

 protected:
  /**
   * Export the given QCT file to the specified path as a GeoTIFF file.
   *
   * @param qct_file The QCT file to export.
   * @param options The export options for the GeoTIFF file.
   */
  void exportToImpl(const QctFile& qct_file, const GeoTiffExportOptions& options) const override;

 private:
  static constexpr std::int32_t EPSG_4326_WGS84{4326};
  static std::once_flag once_flag_;

  /**
 * Set the context search paths of PROJ. Intended to be called exactly once before using PROJ.
 */
  static void setProjContextSearchPaths();

  /**
   * Set the geographic transformation of the GDAL dataset from the QCT file.
   * Uses a grid of Ground Control Points (GCPs), computed with the georeferencing information of the QCT file, to set the geographic transformation.
   * @param qct_file the QCT file
   * @param gdal_dataset the GDAL dataset to set the geographic transformation of
   */
  static void setGroundControlPoints(const QctFile& qct_file, GDALDataset& gdal_dataset);

  static void setGeoTransform(const QctFile& qct_file, GDALDataset& gdal_dataset);

  /**
   * Set the projection of the GDAL dataset.
   * @param gdal_dataset the GDAL dataset to set the projection of
   */
  static void setProjection(GDALDataset& gdal_dataset);

  /**
   * Write the raster bands of a QCT file to a GeoTIFF file.
   * @param qct_file the QCT file
   * @param gdal_dataset the GDAL dataset to write the raster bands to
   */
  static void writeRasterBands(const QctFile& qct_file, GDALDataset& gdal_dataset);
};

std::once_flag GeoTiffExporter::once_flag_{};

GeoTiffExporter::GeoTiffExporter() {
  std::call_once(once_flag_, setProjContextSearchPaths);
}

void GeoTiffExporter::exportToImpl(const QctFile& qct_file, const GeoTiffExportOptions& options) const {
  constexpr auto driver_name{"GTiff"};
  GDALAllRegister();
  GDALDriver* gdal_driver = GetGDALDriverManager()->GetDriverByName(driver_name);
  if (gdal_driver == nullptr) {
    throw QctExportException{std::format("Failed to obtain {} driver", driver_name)};
  }
  GDALDataset* gdal_dataset = gdal_driver->Create(options.path.string().c_str(), qct_file.width(), qct_file.height(),
                                                  palette::COLOR_CHANNELS, GDT_Byte, nullptr);
  if (gdal_dataset == nullptr) {
    throw QctExportException{"Failed to create GDAL-dataset"};
  }
  switch (options.georef_method) {
    case GeoTiffExportOptions::GeorefMethod::AUTOMATIC: {
      if (qct_file.georef.coefficients.anyNonZeroLonLatSecondOrThirdOrderTerms()) {
        std::cout << "Georeferencing using GCPs (Ground Control Points)." << std::endl;
        setGroundControlPoints(qct_file, *gdal_dataset);
      } else {
        std::cout << "Georeferencing using a linear affine transformation." << std::endl;
        setGeoTransform(qct_file, *gdal_dataset);
      }
    } break;
    case GeoTiffExportOptions::GeorefMethod::GCP: {
      std::cout << "Georeferencing using GCPs (Ground Control Points)." << std::endl;
      setGroundControlPoints(qct_file, *gdal_dataset);
    } break;
    case GeoTiffExportOptions::GeorefMethod::LINEAR: {
      std::cout << "Georeferencing using a linear affine transformation." << std::endl;
      setGeoTransform(qct_file, *gdal_dataset);
    } break;
    default:
      throw std::logic_error{"Unknown GeoTiffExportOptions::GeorefMethod"};
  }
  setProjection(*gdal_dataset);
  writeRasterBands(qct_file, *gdal_dataset);
  GDALClose(gdal_dataset);
}

void GeoTiffExporter::setProjContextSearchPaths() {
  constexpr std::array<const char*, 1> search_paths{{MY_PROJ_DIR}};
  proj_context_set_search_paths(nullptr, 1, search_paths.data());
}

void GeoTiffExporter::setGroundControlPoints(const QctFile& qct_file, GDALDataset& gdal_dataset) {
  constexpr std::int32_t grid_size = 5;
  const auto datum_shift = qct_file.metadata.extended_data.datum_shift;
  std::vector<GDAL_GCP> ground_control_points{};
  ground_control_points.reserve(grid_size * grid_size);
  for (std::int32_t i = 0; i < grid_size; ++i) {
    for (std::int32_t j = 0; j < grid_size; ++j) {
      const double x = i * (qct_file.width() - 1) / (grid_size - 1.0);
      const double y = j * (qct_file.height() - 1) / (grid_size - 1.0);
      const auto [longitude, latitude] = qct_file.georef.toWgs84Coordinates({x, y}, datum_shift);
      ground_control_points.push_back(GDAL_GCP{.pszId = nullptr,
                                               .pszInfo = nullptr,
                                               .dfGCPPixel = x,
                                               .dfGCPLine = y,
                                               .dfGCPX = longitude,
                                               .dfGCPY = latitude,
                                               .dfGCPZ = 0.0});
    }
  }
  OGRSpatialReference spatial_reference{};
  spatial_reference.importFromEPSG(EPSG_4326_WGS84);
  if (gdal_dataset.SetGCPs(static_cast<std::int32_t>(ground_control_points.size()), ground_control_points.data(),
                           spatial_reference.exportToWkt().c_str()) != CE_None) {
    throw QctExportException{"Failed to set GCPs"};
  }
}
void GeoTiffExporter::setGeoTransform(const QctFile& qct_file, GDALDataset& gdal_dataset) {
  std::array<double, 6> geo_transform{{
      qct_file.georef.coefficients.lon,
      qct_file.georef.coefficients.lon_x,
      qct_file.georef.coefficients.lon_y,
      qct_file.georef.coefficients.lat,
      qct_file.georef.coefficients.lat_x,
      qct_file.georef.coefficients.lat_y,
  }};
  gdal_dataset.SetGeoTransform(geo_transform.data());
}

void GeoTiffExporter::setProjection(GDALDataset& gdal_dataset) {
  OGRSpatialReference spatial_reference{};
  spatial_reference.importFromEPSG(EPSG_4326_WGS84);
  gdal_dataset.SetProjection(spatial_reference.exportToWkt().c_str());
}

void GeoTiffExporter::writeRasterBands(const QctFile& qct_file, GDALDataset& gdal_dataset) {
  for (std::int32_t channel_index = 0; channel_index < palette::COLOR_CHANNELS; ++channel_index) {
    auto band_bytes = qct_file.image_index.channelBytes(channel_index);
    if (GDALRasterBand* gdal_raster_band = gdal_dataset.GetRasterBand(channel_index + 1);
        gdal_raster_band->RasterIO(GF_Write, 0, 0, qct_file.width(), qct_file.height(), band_bytes.data(),
                                   qct_file.width(), qct_file.height(), GDT_Byte, 0, 0) != CE_None) {
      throw QctExportException{"Error writing raster data."};
    }
  }
}

}  // namespace qct::ex
