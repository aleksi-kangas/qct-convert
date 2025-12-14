package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.Parseable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <pre>
 * +--------+-----------+-----------+
 * | Offset | Data Type | Content   |
 * +--------+-----------+-----------+
 * | 0x00   | Double    | Latitude  |
 * | 0x08   | Double    | Longitude |
 * | ...    | ...       | ...       |
 * +--------+-----------+-----------+
 * </pre>
 */
public record MapOutline(Point[] points) implements Parseable {
  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final MapOutline that = (MapOutline) o;
    return Objects.deepEquals(points, that.points);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(points);
  }

  @Nonnull
  @Override
  public String toString() {
    return Arrays.stream(points).map(p -> "\t\t" + p).collect(Collectors.joining("\n"));
  }

  public record Point(double latitude,
                      double longitude) implements Parseable {
    @Nonnull
    @Override
    public String toString() {
      return String.format("{Lat: %.6f, Lot: %.6f}", latitude, longitude);
    }
  }
}