package com.github.aleksikangas.qct.core.meta.parsers;

import com.github.aleksikangas.qct.core.meta.*;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.time.Instant;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link Metadata}.
 */
public final class MetadataParser implements Parser<Metadata, MetadataParser.Task> {
  @Nonnull
  @Override
  public Class<Metadata> parseableClass() {
    return Metadata.class;
  }

  @Nonnull
  @Override
  public Metadata execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     ParserRegistry parserRegistry) implements ParseTask<Metadata>, AsyncReadable, ParserRegistryAware {
    @Nonnull
    @Override
    public Class<Metadata> parseableClass() {
      return Metadata.class;
    }

    @Nonnull
    @Override
    public Metadata parse() {
      return new Metadata(MagicNumber.of(QctReader.readInt(asyncFileChannel, Metadata.BYTE_OFFSET)),
                          FileFormatVersion.of(QctReader.readInt(asyncFileChannel, Metadata.BYTE_OFFSET + 0x04L)),
                          QctReader.readInt(asyncFileChannel, Metadata.BYTE_OFFSET + 0x08L),
                          QctReader.readInt(asyncFileChannel, Metadata.BYTE_OFFSET + 0x0CL),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x10L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x14L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x18L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x1CL),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x20L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x24L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x28L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x2CL),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x30L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x34L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x38L),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x3CL),
                          QctReader.readStringFromPointer(asyncFileChannel, Metadata.BYTE_OFFSET + 0x44L),
                          QctReader.readInt(asyncFileChannel, Metadata.BYTE_OFFSET + 0x48L),
                          Instant.ofEpochSecond(QctReader.readInt(asyncFileChannel, Metadata.BYTE_OFFSET + 0x4CL)),
                          parserRegistry.getParser(ExtendedData.class)
                                        .execute(new ExtendedDataParser.Task(asyncFileChannel,
                                                                             QctReader.readPointer(asyncFileChannel,
                                                                                                   Metadata.BYTE_OFFSET +
                                                                                                   0x54L),
                                                                             parserRegistry)),
                          parserRegistry.getParser(MapOutline.class)
                                        .execute(new MapOutlineParser.Task(asyncFileChannel,
                                                                           Metadata.BYTE_OFFSET + 0x58L)));
    }
  }
}
