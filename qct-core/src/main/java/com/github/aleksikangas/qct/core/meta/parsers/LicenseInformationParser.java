package com.github.aleksikangas.qct.core.meta.parsers;

import com.github.aleksikangas.qct.core.meta.LicenseInformation;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link LicenseInformation}.
 */
public final class LicenseInformationParser implements Parser<LicenseInformation, LicenseInformationParser.Task> {
  @Nonnull
  @Override
  public Class<LicenseInformation> parseableClass() {
    return LicenseInformation.class;
  }

  @Nonnull
  @Override
  public LicenseInformation execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     ParserRegistry parserRegistry)
          implements ParseTask<LicenseInformation>, AsyncReadable, ByteOffsetAware, ParserRegistryAware {
    @Nonnull
    @Override
    public Class<LicenseInformation> parseableClass() {
      return LicenseInformation.class;
    }

    @Nonnull
    @Override
    public LicenseInformation parse() {
      return new LicenseInformation(QctReader.readInt(asyncFileChannel, byteOffset),
                                    QctReader.readString(asyncFileChannel, byteOffset + 0x0CL),
                                    parserRegistry.parse(new SerialNumberParser.Task(asyncFileChannel,
                                                                                     QctReader.readPointer(
                                                                                             asyncFileChannel,
                                                                                             byteOffset + 0x10L))));
    }
  }
}
