module;

#include <array>
#include <cstdint>
#include <filesystem>
#include <format>
#include <mutex>
#include <vector>

#include "gdal_priv.h"
#include "gdalwarper.h"
#include "proj.h"

export module qctexport:geotiff;

import qct;

import :exception;
import :exporter;

namespace qct::ex {
/**
 * Exporter for GeoTIFF files.
 */
class GeoTiffExporter final : public AbstractExporter<GeoTiffExporter> {
 public:
  GeoTiffExporter();

  /**
   * Export the given QCT file to the specified path as a GeoTIFF file.
   *
   * @param qct_file The QCT file to export.
   * @param path The path where the exported GeoTIFF file should be saved.
   */
  void exportTo(const QctFile& qct_file, const std::filesystem::path& path) const;

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
  void setGeoTransform(const QctFile& qct_file, GDALDataset& gdal_dataset) const;

  /**
   * Set the projection of the GDAL dataset.
   * @param gdal_dataset the GDAL dataset to set the projection of
   */
  void setProjection(GDALDataset& gdal_dataset) const;

  /**
   * Write the raster bands of a QCT file to a GeoTIFF file.
   * @param qct_file the QCT file
   * @param gdal_dataset the GDAL dataset to write the raster bands to
   */
  void writeRasterBands(const QctFile& qct_file, GDALDataset& gdal_dataset) const;
};

std::once_flag GeoTiffExporter::once_flag_{};

GeoTiffExporter::GeoTiffExporter() {
  std::call_once(once_flag_, setProjContextSearchPaths);
}

void GeoTiffExporter::exportTo(const QctFile& qct_file, const std::filesystem::path& path) const {
  constexpr auto driver_name{"GTiff"};
  GDALAllRegister();
  GDALDriver* gdal_driver = GetGDALDriverManager()->GetDriverByName(driver_name);
  if (gdal_driver == nullptr) {
    throw QctExportException{std::format("Failed to obtain {} driver", driver_name)};
  }
  GDALDataset* gdal_dataset = gdal_driver->Create(path.string().c_str(), qct_file.width(), qct_file.height(),
                                                  palette::COLOR_CHANNELS, GDT_Byte, nullptr);
  if (gdal_dataset == nullptr) {
    throw QctExportException{"Failed to create GDAL-dataset"};
  }
  setGeoTransform(qct_file, *gdal_dataset);
  setProjection(*gdal_dataset);
  writeRasterBands(qct_file, *gdal_dataset);
  GDALClose(gdal_dataset);
}

void GeoTiffExporter::setProjContextSearchPaths() {
  constexpr std::array<const char*, 1> search_paths{{MY_PROJ_DIR}};
  proj_context_set_search_paths(nullptr, 1, search_paths.data());
}

void GeoTiffExporter::setGeoTransform(const QctFile& qct_file, GDALDataset& gdal_dataset) const {
  constexpr std::int32_t grid_size = 100;
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

void GeoTiffExporter::setProjection(GDALDataset& gdal_dataset) const {
  OGRSpatialReference spatial_reference{};
  spatial_reference.importFromEPSG(EPSG_4326_WGS84);
  gdal_dataset.SetProjection(spatial_reference.exportToWkt().c_str());
}

void GeoTiffExporter::writeRasterBands(const QctFile& qct_file, GDALDataset& gdal_dataset) const {
  for (std::int32_t channel_index = 0; channel_index < palette::COLOR_CHANNELS; ++channel_index) {
    auto band_bytes = qct_file.image_index.channelBytes(channel_index);
    GDALRasterBand* gdal_raster_band = gdal_dataset.GetRasterBand(channel_index + 1);  // [1, n]
    gdal_raster_band->RasterIO(GF_Write, 0, 0, qct_file.width(), qct_file.height(), band_bytes.data(), qct_file.width(),
                               qct_file.height(), GDT_Byte, 0, 0);
  }
}

}  // namespace qct::ex
