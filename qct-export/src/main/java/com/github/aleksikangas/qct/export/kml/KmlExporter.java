package com.github.aleksikangas.qct.export.kml;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.meta.MapOutline;
import com.github.aleksikangas.qct.export.QctExportRuntimeException;
import com.github.aleksikangas.qct.export.gdal.GdalRegistration;
import org.gdal.ogr.*;
import org.gdal.osr.SpatialReference;

import java.nio.file.Path;
import java.util.Arrays;

public final class KmlExporter {
  private static final int EPSG_4326_WGS84 = 4326;
  private static final String KML_DRIVER_NAME = "KML";

  public static void exportKml(final QctFile qctFile, final Path kmlPath) throws QctExportRuntimeException {
    try {
      GdalRegistration.register();
      final Driver driver = ogr.GetDriverByName(KML_DRIVER_NAME);
      if (driver == null) {
        throw new QctExportRuntimeException(String.format("'%s' driver not found", KML_DRIVER_NAME));
      }
      final DataSource dataSource = driver.CreateDataSource(kmlPath.toString());
      if (dataSource == null) {
        throw new QctExportRuntimeException("KML data source could not be created");
      }
      final var spatialReference = new SpatialReference();
      spatialReference.ImportFromEPSG(EPSG_4326_WGS84);
      final Layer layer = dataSource.CreateLayer("Map_Outline", spatialReference, ogr.wkbLineString, null);
      if (layer == null) {
        throw new QctExportRuntimeException("KML layer could not be created");
      }
      final var feature = new Feature(layer.GetLayerDefn());
      feature.SetField("Name", "Map Outline");
      feature.SetGeometry(mapOutlineGeometry(qctFile));
      feature.SetStyleString("PEN(c:#00FF00,w:2px);BRUSH(fc:#80FF0000)");
      if (layer.CreateFeature(feature) != 0) {
        throw new QctExportRuntimeException("KML feature could not be created");
      }
      layer.SyncToDisk();
      dataSource.delete();
    } catch (final Throwable t) {
      throw new QctExportRuntimeException(t);
    }
  }

  private static Geometry mapOutlineGeometry(final QctFile qctFile) {
    final var line = new Geometry(ogr.wkbLineString);
    for (final MapOutline.Point point : qctFile.metadata().mapOutline().points()) {
      line.AddPoint(point.latitude(), point.longitude());
    }
    Arrays.stream(qctFile.metadata().mapOutline().points())
          .findFirst()
          .ifPresent(point -> line.AddPoint(point.latitude(), point.longitude()));
    return line;
  }

  private KmlExporter() {
  }
}
