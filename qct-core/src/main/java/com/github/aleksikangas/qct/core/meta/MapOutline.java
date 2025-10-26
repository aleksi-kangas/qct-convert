package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.reader.QctReader;

import java.nio.channels.AsynchronousFileChannel;
import java.util.Arrays;
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
  public String toString() {
    return Arrays.stream(points)
                 .map(p -> "\t\t" + p)
                 .collect(Collectors.joining("\n"));
  }

  public static final class Parser extends AbstractParser<MapOutline> {
    @Override
    public Class<MapOutline> parseableClass() {
      return MapOutline.class;
    }

    @Override
    public MapOutline parse(final AsynchronousFileChannel asyncFileChannel,
                            final long byteOffset,
                            final ParserRegistry parserRegistry) {
      final int pointCount = QctReader.readInt(asyncFileChannel, byteOffset);
      final int arrayByteOffset = QctReader.readPointer(asyncFileChannel, byteOffset + 0x04L);
      final Point[] points = new Point[pointCount];
      for (int i = 0; i < pointCount; ++i) {
        points[i] = parserRegistry.getParser(Point.class)
                                  .parse(asyncFileChannel, arrayByteOffset + i * (0x08L + 0x08L), parserRegistry);
      }
      return new MapOutline(points);
    }
  }

  public record Point(double latitude,
                      double longitude) implements Parseable {
    @Override
    public String toString() {
      return String.format("{Lat: %.6f, Lot: %.6f}", latitude, longitude);
    }

    public static final class Parser extends AbstractParser<Point> {
      @Override
      public Class<Point> parseableClass() {
        return Point.class;
      }

      @Override
      public Point parse(final AsynchronousFileChannel asyncFileChannel,
                         final long byteOffset,
                         final ParserRegistry parserRegistry) {
        return new Point(QctReader.readDouble(asyncFileChannel, byteOffset),
                         QctReader.readDouble(asyncFileChannel, byteOffset + 0x08L));
      }
    }
  }
}
