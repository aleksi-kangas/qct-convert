package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.DatumShift;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link DatumShift}.
 */
@ApplicationScoped
public class DatumShiftParser extends AbstractParser<DatumShift, DatumShiftParser.Task> {
  @Nonnull
  @Override
  public Class<DatumShift> parseableClass() {
    return DatumShift.class;
  }

  @Nonnull
  @Override
  public DatumShift parse(final Task parseTask) {
    return new DatumShift(QctReader.readDouble(parseTask.asyncFileChannel, parseTask.byteOffset),
                          QctReader.readDouble(parseTask.asyncFileChannel, parseTask.byteOffset + 0x08L));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<DatumShift>, AsyncFileChannelAware, ByteOffsetAware {
  }
}
