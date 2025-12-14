package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.DigitalMapShop;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link DigitalMapShop}.
 */
@ApplicationScoped
public class DigitalMapShopParser extends AbstractParser<DigitalMapShop, DigitalMapShopParser.Task> {
  @Nonnull
  @Override
  public Class<DigitalMapShop> parseableClass() {
    return DigitalMapShop.class;
  }

  @Nonnull
  @Override
  public DigitalMapShop parse(final Task parseTask) {
    return new DigitalMapShop(QctReader.readInt(parseTask.asyncFileChannel, parseTask.byteOffset),
                              QctReader.readStringFromPointer(parseTask.asyncFileChannel,
                                                              parseTask.byteOffset + 0x04L));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask, AsyncFileChannelAware, ByteOffsetAware {
  }
}
