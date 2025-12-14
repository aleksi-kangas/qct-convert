package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.ExtendedData;
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
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ExtendedData}.
 */
@ApplicationScoped
public class ExtendedDataParser extends AbstractParser<ExtendedData, ExtendedDataParser.Task> {
  private final DatumShiftParser datumShiftParser;
  private final DigitalMapShopParser digitalMapShopParser;
  private final LicenseInformationParser licenseInformationParser;

  @Inject
  public ExtendedDataParser(final DatumShiftParser datumShiftParser,
                            final DigitalMapShopParser digitalMapShopParser,
                            final LicenseInformationParser licenseInformationParser) {
    this.datumShiftParser = Objects.requireNonNull(datumShiftParser);
    this.digitalMapShopParser = Objects.requireNonNull(digitalMapShopParser);
    this.licenseInformationParser = Objects.requireNonNull(licenseInformationParser);
  }

  @Nonnull
  @Override
  public Class<ExtendedData> parseableClass() {
    return ExtendedData.class;
  }

  @Nonnull
  @Override
  public ExtendedData parse(final Task parseTask) {
    return new ExtendedData(QctReader.readStringFromPointer(parseTask.asyncFileChannel, parseTask.byteOffset),
                            datumShiftParser.parse(new DatumShiftParser.Task(parseTask.asyncFileChannel,
                                                                             QctReader.readPointer(parseTask.asyncFileChannel,
                                                                                                   parseTask.byteOffset + 0x04L))),
                            QctReader.readStringFromPointer(parseTask.asyncFileChannel, parseTask.byteOffset + 0x08L),
                            licenseInformationParser.parse(new LicenseInformationParser.Task(parseTask.asyncFileChannel,
                                                                                             QctReader.readPointer(
                                                                                                 parseTask.asyncFileChannel,
                                                                                                 parseTask.byteOffset + 0x14L))),
                            QctReader.readStringFromPointer(parseTask.asyncFileChannel, parseTask.byteOffset + 0x18L),
                            digitalMapShopParser.parse(new DigitalMapShopParser.Task(parseTask.asyncFileChannel,
                                                                                     QctReader.readPointer(parseTask.asyncFileChannel,
                                                                                                           parseTask.byteOffset + 0x1CL))));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset) implements ParseTask, AsyncFileChannelAware, ByteOffsetAware {
  }
}
