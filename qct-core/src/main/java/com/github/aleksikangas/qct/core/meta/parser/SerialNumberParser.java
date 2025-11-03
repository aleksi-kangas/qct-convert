package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.SerialNumber;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link SerialNumber}.
 */
public final class SerialNumberParser extends AbstractParser<SerialNumber, SerialNumberParser.Task> {
  public SerialNumberParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<SerialNumber> parseableClass() {
    return SerialNumber.class;
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
