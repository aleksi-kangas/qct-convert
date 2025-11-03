package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.DatumShift;
import com.github.aleksikangas.qct.core.meta.DigitalMapShop;
import com.github.aleksikangas.qct.core.meta.ExtendedData;
import com.github.aleksikangas.qct.core.meta.LicenseInformation;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ExtendedData}.
 */
public final class ExtendedDataParser extends AbstractParser<ExtendedData, ExtendedDataParser.Task> {
  public ExtendedDataParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<ExtendedData> parseableClass() {
    return ExtendedData.class;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     ParserRegistry parserRegistry)
      implements ParseTask<ExtendedData>, AsyncReadable, ByteOffsetAware, ParserRegistryAware {
    @Nonnull
    @Override
    public Class<ExtendedData> parseableClass() {
      return ExtendedData.class;
    }

    @Nonnull
    @Override
    public ExtendedData parse() {
      return new ExtendedData(QctReader.readStringFromPointer(asyncFileChannel, byteOffset),
                              parserRegistry.getParser(DatumShift.class)
                                  .execute(new DatumShiftParser.Task(asyncFileChannel,
                                                                     QctReader.readPointer(asyncFileChannel,
                                                                                           byteOffset +
                                                                                               0x04L))),
                              QctReader.readStringFromPointer(asyncFileChannel, byteOffset + 0x08L),
                              parserRegistry.getParser(LicenseInformation.class)
                                  .execute(new LicenseInformationParser.Task(asyncFileChannel,
                                                                             QctReader.readPointer(
                                                                                 asyncFileChannel,
                                                                                 byteOffset + 0x14L),
                                                                             parserRegistry)),
                              QctReader.readStringFromPointer(asyncFileChannel, byteOffset + 0x18L),
                              parserRegistry.getParser(DigitalMapShop.class)
                                  .execute(new DigitalMapShopParser.Task(asyncFileChannel,
                                                                         QctReader.readPointer(
                                                                             asyncFileChannel,
                                                                             byteOffset + 0x1CL))));
    }
  }
}
