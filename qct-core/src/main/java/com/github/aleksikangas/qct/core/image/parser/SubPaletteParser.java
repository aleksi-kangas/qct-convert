package com.github.aleksikangas.qct.core.image.parser;

import com.github.aleksikangas.qct.core.image.color.SubPalette;
import com.github.aleksikangas.qct.core.image.color.SubPaletteSizeType;
import com.github.aleksikangas.qct.core.image.parser.task.SubPaletteSizeTypeAware;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link SubPalette}.
 */
@ApplicationScoped
public class SubPaletteParser extends AbstractParser<SubPalette, SubPaletteParser.Task> {
  @Nonnull
  @Override
  public Class<SubPalette> parseableClass() {
    return SubPalette.class;
  }

  @Nonnull
  @Override
  public SubPalette parse(final Task parseTask) {
    final int size = switch (parseTask.subPaletteSizeType) {
      case NORMAL -> QctReader.readByte(parseTask.asyncFileChannel, parseTask.byteOffset);
      case INVERSE -> 256 - QctReader.readByte(parseTask.asyncFileChannel, parseTask.byteOffset);
    };
    final int[] paletteIndices = QctReader.readBytes(parseTask.asyncFileChannel, parseTask.byteOffset + 0x01L, size);
    return new SubPalette(size, paletteIndices);
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     SubPaletteSizeType subPaletteSizeType) implements ParseTask, AsyncFileChannelAware, ByteOffsetAware, SubPaletteSizeTypeAware {
  }
}
