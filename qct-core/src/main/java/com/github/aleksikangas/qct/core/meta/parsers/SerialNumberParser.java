package com.github.aleksikangas.qct.core.meta.parsers;

import com.github.aleksikangas.qct.core.meta.SerialNumber;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link SerialNumber}.
 */
public final class SerialNumberParser implements Parser<SerialNumber, SerialNumberParser.Task> {
  @Nonnull
  @Override
  public Class<SerialNumber> parseableClass() {
    return SerialNumber.class;
  }

  @Nonnull
  @Override
  public SerialNumber execute(final Task parseTask) {
    return parseTask.parse();
  }


  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<SerialNumber>, AsyncReadable, ByteOffsetAware {
    @Nonnull
    @Override
    public Class<SerialNumber> parseableClass() {
      return SerialNumber.class;
    }

    @Nonnull
    @Override
    public SerialNumber parse() {
      return new SerialNumber(QctReader.readBytes(asyncFileChannel, byteOffset, 32));
    }
  }
}
