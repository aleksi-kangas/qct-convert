package com.github.aleksikangas.qct.export.geotiff;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.georef.GeoreferencingCoefficients;
import com.github.aleksikangas.qct.core.image.util.ImageIndexUtils;
import com.github.aleksikangas.qct.core.meta.DatumShift;
import com.github.aleksikangas.qct.export.QctExportRuntimeException;
import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.SpatialReference;

import javax.annotation.concurrent.GuardedBy;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class GeoTiffExporter {
  private static final int BAND_COUNT = 3;
  private static final int EPSG_4326_WGS84 = 4326;

  private static final Object GDAL_REGISTER_LOCK = new Object();
  @GuardedBy("GDAL_REGISTER_LOCK")
  private static boolean isGdalRegistered = false;

  public static void exportGeoTiff(final QctFile qctFile, final Path geoTiffPath) throws QctExportRuntimeException {
    registerGdal();
    try {
      final Driver driver = gdal.GetDriverByName("GTiff");
      if (driver == null) {
        throw new QctExportRuntimeException("'GTiff' driver not found");
      }
      final Dataset dataset = driver.Create(geoTiffPath.toString(),
                                            qctFile.width(),
                                            qctFile.height(),
                                            BAND_COUNT,
                                            gdalconstConstants.GDT_Byte);
      if (dataset == null) {
        throw new QctExportRuntimeException("GeoTIFF dataset could not be created");
      }
      setGeoTransform(dataset, qctFile);
      setProjection(dataset);
      writeRasterBands(dataset, qctFile);
      dataset.Close();
    } catch (final Throwable t) {
      throw new QctExportRuntimeException(t);
    }
  }

  public static CompletableFuture<Void> exportGeoTiffAsync(final QctFile qctFile, final Path pngPath) {
    return CompletableFuture.runAsync(() -> exportGeoTiff(qctFile, pngPath));
  }

  private static void setGeoTransform(final Dataset dataset, final QctFile qctFile) {
    final DatumShift datumShift = qctFile.metadata().extendedData().datumShift();
    final GeoreferencingCoefficients georeferencingCoefficients = qctFile.georeferencingCoefficients();
    final GeoreferencingCoefficients.Wgs84Coordinates topLeftWgs84 = georeferencingCoefficients.toWgs84(new GeoreferencingCoefficients.ImageCoordinates(
        0,
        0), datumShift);
    final double[] geoTransform = new double[]{topLeftWgs84.longitude(), georeferencingCoefficients.lonX(), georeferencingCoefficients.lonY(), topLeftWgs84.latitude(), georeferencingCoefficients.latX(), georeferencingCoefficients.latY(),};
    dataset.SetGeoTransform(geoTransform);
  }

  private static void setProjection(final Dataset dataset) {
    final var spatialReference = new SpatialReference();
    spatialReference.ImportFromEPSG(EPSG_4326_WGS84);
    dataset.SetProjection(spatialReference.ExportToWkt());
  }

  private static void writeRasterBands(final Dataset dataset, final QctFile qctFile) {
    final Band redBand = dataset.GetRasterBand(1);
    redBand.WriteRaster(0,
                        0,
                        qctFile.width(),
                        qctFile.height(),
                        ImageIndexUtils.channelValues(qctFile, ImageIndexUtils.Channel.RED));
    final Band greenBand = dataset.GetRasterBand(2);
    greenBand.WriteRaster(0,
                          0,
                          qctFile.width(),
                          qctFile.height(),
                          ImageIndexUtils.channelValues(qctFile, ImageIndexUtils.Channel.GREEN));
    final Band blueBand = dataset.GetRasterBand(3);
    blueBand.WriteRaster(0,
                         0,
                         qctFile.width(),
                         qctFile.height(),
                         ImageIndexUtils.channelValues(qctFile, ImageIndexUtils.Channel.BLUE));
  }

  private static void registerGdal() {
    try {
      synchronized (GDAL_REGISTER_LOCK) {
        if (!isGdalRegistered) {
          gdal.AllRegister();
          isGdalRegistered = true;
        }
      }
    } catch (final Throwable t) {
      throw new QctExportRuntimeException(t);
    }
  }

  private GeoTiffExporter() {
  }
}
