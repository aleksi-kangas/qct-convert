package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.DatumShift;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link DatumShift}.
 */
public final class DatumShiftParser extends AbstractParser<DatumShift, DatumShiftParser.Task> {
  public DatumShiftParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<DatumShift> parseableClass() {
    return DatumShift.class;
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
