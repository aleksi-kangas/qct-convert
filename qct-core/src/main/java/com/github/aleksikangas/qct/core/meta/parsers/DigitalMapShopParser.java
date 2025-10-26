package com.github.aleksikangas.qct.core.meta.parsers;

import com.github.aleksikangas.qct.core.meta.DigitalMapShop;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link DigitalMapShop}.
 */
public final class DigitalMapShopParser implements Parser<DigitalMapShop, DigitalMapShopParser.Task> {
  @Nonnull
  @Override
  public Class<DigitalMapShop> parseableClass() {
    return DigitalMapShop.class;
  }

  @Nonnull
  @Override
  public DigitalMapShop execute(final Task parseTask) {
    return parseTask.parse();
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
