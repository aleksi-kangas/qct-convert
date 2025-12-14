package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.SerialNumber;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link SerialNumber}.
 */
@ApplicationScoped
public class SerialNumberParser extends AbstractParser<SerialNumber, SerialNumberParser.Task> {
  @Nonnull
  @Override
  public Class<SerialNumber> parseableClass() {
    return SerialNumber.class;
  }

  @Nonnull
  @Override
  public SerialNumber parse(final Task parseTask) {
    return new SerialNumber(QctReader.readBytes(parseTask.asyncFileChannel, parseTask.byteOffset, 32));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask, AsyncFileChannelAware, ByteOffsetAware {
  }
}
