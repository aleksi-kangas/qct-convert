package com.github.aleksikangas.qct.core.meta.parsers;

import com.github.aleksikangas.qct.core.meta.MapOutline;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link MapOutline}.
 */
public final class MapOutlineParser implements Parser<MapOutline, MapOutlineParser.Task> {
  @Nonnull
  @Override
  public Class<MapOutline> parseableClass() {
    return MapOutline.class;
  }

  @Nonnull
  @Override
  public MapOutline execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<MapOutline>, AsyncReadable, ByteOffsetAware {
    @Nonnull
    @Override
    public Class<MapOutline> parseableClass() {
      return MapOutline.class;
    }

    @Nonnull
    @Override
    public MapOutline parse() {
      final int pointCount = QctReader.readInt(asyncFileChannel, byteOffset);
      final int arrayByteOffset = QctReader.readPointer(asyncFileChannel, byteOffset + 0x04L);
      final MapOutline.Point[] points = new MapOutline.Point[pointCount];
      for (int i = 0; i < pointCount; ++i) {
        final long pointByteOffset = arrayByteOffset + i * (0x08L + 0x08L);
        points[i] = new MapOutline.Point(QctReader.readDouble(asyncFileChannel, pointByteOffset),
                                         QctReader.readDouble(asyncFileChannel, pointByteOffset + 0x08L));
      }
      return new MapOutline(points);
    }
  }
}
