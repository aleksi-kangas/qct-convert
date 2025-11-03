package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.DigitalMapShop;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link DigitalMapShop}.
 */
public final class DigitalMapShopParser extends AbstractParser<DigitalMapShop, DigitalMapShopParser.Task> {
  public DigitalMapShopParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<DigitalMapShop> parseableClass() {
    return DigitalMapShop.class;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<DigitalMapShop>, AsyncReadable, ByteOffsetAware {
    @Nonnull
    @Override
    public Class<DigitalMapShop> parseableClass() {
      return DigitalMapShop.class;
    }

    @Nonnull
    @Override
    public DigitalMapShop parse() {
      return new DigitalMapShop(QctReader.readInt(asyncFileChannel, byteOffset),
                                QctReader.readStringFromPointer(asyncFileChannel, byteOffset + 0x04L));
    }
  }
}
