package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.MapOutline;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link MapOutline}.
 */
@ApplicationScoped
public class MapOutlineParser extends AbstractParser<MapOutline, MapOutlineParser.Task> {
  @Nonnull
  @Override
  public Class<MapOutline> parseableClass() {
    return MapOutline.class;
  }

  @Nonnull
  @Override
  public MapOutline parse(final Task parseTask) {
    final int pointCount = QctReader.readInt(parseTask.asyncFileChannel, parseTask.byteOffset);
    final int arrayByteOffset = QctReader.readPointer(parseTask.asyncFileChannel, parseTask.byteOffset + 0x04L);
    final MapOutline.Point[] points = new MapOutline.Point[pointCount];
    for (int i = 0; i < pointCount; ++i) {
      final long pointByteOffset = arrayByteOffset + i * (0x08L + 0x08L);
      points[i] = new MapOutline.Point(QctReader.readDouble(parseTask.asyncFileChannel, pointByteOffset),
                                       QctReader.readDouble(parseTask.asyncFileChannel, pointByteOffset + 0x08L));
    }
    return new MapOutline(points);
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<MapOutline>, AsyncFileChannelAware, ByteOffsetAware {
  }
}
