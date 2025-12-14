package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.LicenseInformation;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.Objects;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link LicenseInformation}.
 */
@ApplicationScoped
public class LicenseInformationParser extends AbstractParser<LicenseInformation, LicenseInformationParser.Task> {
  private final SerialNumberParser serialNumberParser;

  @Inject
  public LicenseInformationParser(final SerialNumberParser serialNumberParser) {
    this.serialNumberParser = Objects.requireNonNull(serialNumberParser);
  }

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
                                  serialNumberParser.parse(new SerialNumberParser.Task(parseTask.asyncFileChannel,
                                                                                       QctReader.readPointer(parseTask.asyncFileChannel,
                                                                                                             parseTask.byteOffset + 0x10L))));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask, AsyncFileChannelAware, ByteOffsetAware {
  }
}
