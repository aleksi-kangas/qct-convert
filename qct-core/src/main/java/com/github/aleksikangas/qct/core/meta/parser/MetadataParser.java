package com.github.aleksikangas.qct.core.meta.parser;

import com.github.aleksikangas.qct.core.meta.FileFormatVersion;
import com.github.aleksikangas.qct.core.meta.Flag;
import com.github.aleksikangas.qct.core.meta.MagicNumber;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.time.Instant;
import java.util.Objects;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link Metadata}.
 */
@ApplicationScoped
public class MetadataParser extends AbstractParser<Metadata, MetadataParser.Task> {
  private final ExtendedDataParser extendedDataParser;
  private final MapOutlineParser mapOutlineParser;

  @Inject
  public MetadataParser(final ExtendedDataParser extendedDataParser, final MapOutlineParser mapOutlineParser) {
    this.extendedDataParser = Objects.requireNonNull(extendedDataParser);
    this.mapOutlineParser = Objects.requireNonNull(mapOutlineParser);
  }

  @Nonnull
  @Override
  public Class<Metadata> parseableClass() {
    return Metadata.class;
  }

  @Nonnull
  @Override
  public Metadata parse(final Task parseTask) {
    return new Metadata(MagicNumber.of(QctReader.readInt(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET)),
                        FileFormatVersion.of(QctReader.readInt(parseTask.asyncFileChannel,
                                                               Metadata.BYTE_OFFSET + 0x04L)),
                        QctReader.readInt(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x08L),
                        QctReader.readInt(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x0CL),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x10L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x14L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x18L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x1CL),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x20L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x24L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x28L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x2CL),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x30L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x34L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x38L),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x3CL),
                        Flag.flagsOf(QctReader.readInt(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x40)),
                        QctReader.readStringFromPointer(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x44L),
                        QctReader.readInt(parseTask.asyncFileChannel, Metadata.BYTE_OFFSET + 0x48L),
                        Instant.ofEpochSecond(QctReader.readInt(parseTask.asyncFileChannel,
                                                                Metadata.BYTE_OFFSET + 0x4CL)),
                        extendedDataParser.parse(new ExtendedDataParser.Task(parseTask.asyncFileChannel,
                                                                             QctReader.readPointer(parseTask.asyncFileChannel,
                                                                                                   Metadata.BYTE_OFFSET + 0x54L))),
                        mapOutlineParser.parse(new MapOutlineParser.Task(parseTask.asyncFileChannel,
                                                                         Metadata.BYTE_OFFSET + 0x58L)));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel) implements ParseTask, AsyncFileChannelAware {
  }
}
