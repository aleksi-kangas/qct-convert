package com.github.aleksikangas.qct.core.meta.parsers;

import com.github.aleksikangas.qct.core.meta.DatumShift;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link DatumShift}.
 */
public final class DatumShiftParser implements Parser<DatumShift, DatumShiftParser.Task> {
  @Nonnull
  @Override
  public Class<DatumShift> parseableClass() {
    return DatumShift.class;
  }

  @Nonnull
  @Override
  public DatumShift execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<DatumShift>, AsyncReadable, ByteOffsetAware {
    @Nonnull
    @Override
    public Class<DatumShift> parseableClass() {
      return DatumShift.class;
    }

    @Nonnull
    @Override
    public DatumShift parse() {
      return new DatumShift(QctReader.readDouble(asyncFileChannel, byteOffset),
                            QctReader.readDouble(asyncFileChannel, byteOffset + 0x08L));
    }
  }
}
