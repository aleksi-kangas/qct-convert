package com.github.aleksikangas.qct.core.georef;

import com.github.aleksikangas.qct.core.meta.DatumShift;
import com.github.aleksikangas.qct.core.parser.Parseable;

import javax.annotation.Nonnull;

/**
 * <pre>
 * +--------+--------------+----------------------------------------------------+
 * | Offset | Size (Bytes) | Content                                            |
 * +--------+--------------+----------------------------------------------------+
 * | 0x0060 | 40 x 8       | Geographical Referencing Coefficients - 40 Doubles |
 * +--------+--------------+----------------------------------------------------+
 *
 * +--------+--------+--------+--------+--------+
 * | Offset | 0x00   | 0x50   | 0xA0   | 0xF0   |
 * +--------+--------+--------+--------+--------+
 * | 0x00   | eas    | nor    | lat    | lon    |
 * | 0x08   | easY   | norY   | latX   | lonX   |
 * | 0x10   | easX   | norX   | latY   | lonY   |
 * | 0x18   | easYY  | norYY  | latXX  | lonXX  |
 * | 0x20   | easXY  | norXY  | latXY  | lonXY  |
 * | 0x28   | easXX  | norXX  | latYY  | lonYY  |
 * | 0x30   | easYYY | norYYY | latXXX | lonXXX |
 * | 0x38   | easYYX | norYYX | latXXY | lonXXY |
 * | 0x40   | easYXX | norYXX | latXYY | lonXYY |
 * | 0x48   | easXXX | norXXX | latYYY | lonYYY |
 * +--------+--------+--------+--------+--------+
 * </pre>
 */
public record GeoreferencingCoefficients(double eas,
                                         double easY,
                                         double easX,
                                         double easYY,
                                         double easXY,
                                         double easXX,
                                         double easYYY,
                                         double easYYX,
                                         double easYXX,
                                         double easXXX,

                                         double nor,
                                         double norY,
                                         double norX,
                                         double norYY,
                                         double norXY,
                                         double norXX,
                                         double norYYY,
                                         double norYYX,
                                         double norYXX,
                                         double norXXX,

                                         double lat,
                                         double latX,
                                         double latY,
                                         double latXX,
                                         double latXY,
                                         double latYY,
                                         double latXXX,
                                         double latXXY,
                                         double latXYY,
                                         double latYYY,

                                         double lon,
                                         double lonX,
                                         double lonY,
                                         double lonXX,
                                         double lonXY,
                                         double lonYY,
                                         double lonXXX,
                                         double lonXXY,
                                         double lonXYY,
                                         double lonYYY) implements Parseable {
  public static final long BYTE_OFFSET = 0x0060L;

  private static final String FORMAT = "%.6f";
  private static final String FORMAT_WITH_LABEL = "%s: " + FORMAT;

  @Nonnull
  @Override
  public String toString() {
    return  // eas
        "\teas: [" +
            String.format(FORMAT, eas) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "Y", easY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "X", easX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YY", easYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XY", easXY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XX", easXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YYY", easYYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YYX", easYYX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YXX", easYXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XXX", easXXX) +
            "]\n" +
            // nor
            "\tnor: [" +
            String.format(FORMAT, nor) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "Y", norY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "X", norX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YY", norYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XY", norXY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XX", norXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YYY", norYYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YYX", norYYX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YXX", norYXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XXX", norXXX) +
            "]\n" +
            // lat
            "\tlat: [" +
            String.format(FORMAT, lat) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "X", latX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "Y", latY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XX", latXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XY", latXY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YY", latYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XXX", latXXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XXY", latXXY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XYY", latXYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YYY", latYYY) +
            "]\n" +
            // lon
            "\tlon: [" +
            String.format(FORMAT, lon) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "X", lonX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "Y", lonY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XX", lonXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XY", lonXY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YY", lonYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XXX", lonXXX) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XXY", lonXXY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "XYY", lonXYY) +
            ", " +
            String.format(FORMAT_WITH_LABEL, "YYY", lonYYY) +
            "]";
  }

  public record Wgs84Coordinates(double latitude,
                                 double longitude) {
  }

  public record ImageCoordinates(double y,
                                 double x) {
  }

  /**
   * Transforms the given {@link ImageCoordinates} -> {@link Wgs84Coordinates}.
   *
   * @param imageCoordinates to transform
   * @param datumShift       to apply
   * @return transformed {@link Wgs84Coordinates}
   */
  public Wgs84Coordinates toWgs84(final ImageCoordinates imageCoordinates, final DatumShift datumShift) {
    final double latitude = latXXX * Math.pow(imageCoordinates.x, 3) +
        latXX * Math.pow(imageCoordinates.x, 2) +
        latX * imageCoordinates.x +
        latYYY * Math.pow(imageCoordinates.y, 3) +
        latYY * Math.pow(imageCoordinates.y, 2) +
        latY * imageCoordinates.y +
        latXXY * Math.pow(imageCoordinates.x, 2) * imageCoordinates.y +
        latXYY * imageCoordinates.x * Math.pow(imageCoordinates.y, 2) +
        latXY * imageCoordinates.x * imageCoordinates.y +
        lat;
    final double longitude = lonXXX * Math.pow(imageCoordinates.x, 3) +
        lonXX * Math.pow(imageCoordinates.x, 2) +
        lonX * imageCoordinates.x +
        lonYYY * Math.pow(imageCoordinates.y, 3) +
        lonYY * Math.pow(imageCoordinates.y, 2) +
        lonY * imageCoordinates.y +
        lonXXY * Math.pow(imageCoordinates.x, 2) * imageCoordinates.y +
        lonXYY * imageCoordinates.x * Math.pow(imageCoordinates.y, 2) +
        lonXY * imageCoordinates.x * imageCoordinates.y +
        lon;
    return new Wgs84Coordinates(latitude + datumShift.north(), longitude + datumShift.east());
  }

  /**
   * Transforms the given {@link Wgs84Coordinates} -> {@link ImageCoordinates}.
   *
   * @param wgs84Coordinates to transform
   * @param datumShift       to apply
   * @return transformed {@link ImageCoordinates}
   * @apiNote
   */
  public ImageCoordinates toImage(final Wgs84Coordinates wgs84Coordinates, final DatumShift datumShift) {
    final double latitude = wgs84Coordinates.latitude - datumShift.north();
    final double longitude = wgs84Coordinates.longitude - datumShift.east();
    final double y = norXXX * Math.pow(longitude, 3) +
        norXX * Math.pow(longitude, 2) +
        norX * longitude +
        norYYY * Math.pow(latitude, 3) +
        norYY * Math.pow(latitude, 2) +
        norY * latitude +
        norYXX * latitude * Math.pow(longitude, 2) +
        norYYX * Math.pow(latitude, 2) * longitude +
        norXY * longitude * latitude +
        nor;
    final double x = easXXX * Math.pow(longitude, 3) +
        easXX * Math.pow(longitude, 2) +
        easX * longitude +
        easYYY * Math.pow(latitude, 3) +
        easYY * Math.pow(latitude, 2) +
        easY * latitude +
        easYXX * latitude * Math.pow(longitude, 2) +
        easYYX * Math.pow(latitude, 2) * longitude +
        easXY * longitude * latitude +
        eas;
    return new ImageCoordinates(y, x);
  }
}
