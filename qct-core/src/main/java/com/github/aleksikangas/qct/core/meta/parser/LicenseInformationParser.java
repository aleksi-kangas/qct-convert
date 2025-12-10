package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.LicenseInformation;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parsers;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link LicenseInformation}.
 */
@ApplicationScoped
public class LicenseInformationParser extends AbstractParser<LicenseInformation, LicenseInformationParser.Task> {
  @Nonnull
  @Override
  public Class<LicenseInformation> parseableClass() {
    return LicenseInformation.class;
  }

  @Nonnull
  @Override
  public LicenseInformation parse(final Task parseTask) {
    return new LicenseInformation(QctReader.readInt(parseTask.asyncFileChannel, parseTask.byteOffset),
                                  QctReader.readString(parseTask.asyncFileChannel, parseTask.byteOffset + 0x0CL),
                                  Parsers.execute(SerialNumberParser.class,
                                                  new SerialNumberParser.Task(parseTask.asyncFileChannel,
                                                                              QctReader.readPointer(parseTask.asyncFileChannel,
                                                                                                    parseTask.byteOffset + 0x10L))));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask<LicenseInformation>, AsyncFileChannelAware, ByteOffsetAware {
  }
}
