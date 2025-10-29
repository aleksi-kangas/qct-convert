package com.github.aleksikangas.qct.core.image.parsers;

import com.github.aleksikangas.qct.core.image.color.SubPalette;
import com.github.aleksikangas.qct.core.image.color.SubPaletteSizeType;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

public final class SubPaletteParser implements Parser<SubPalette, SubPaletteParser.Task> {
  @Nonnull
  @Override
  public Class<SubPalette> parseableClass() {
    return SubPalette.class;
  }

  @Nonnull
  @Override
  public SubPalette execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     SubPaletteSizeType subPaletteSizeType)
      implements ParseTask<SubPalette>, AsyncReadable, ByteOffsetAware, SubPaletteSizeTypeAware {
    @Nonnull
    @Override
    public Class<SubPalette> parseableClass() {
      return SubPalette.class;
    }

    @Nonnull
    @Override
    public SubPalette parse() {
      final int size = switch (subPaletteSizeType) {
        case NORMAL -> QctReader.readByte(asyncFileChannel, byteOffset);
        case INVERSE -> 256 - QctReader.readByte(asyncFileChannel, byteOffset);
      };
      final int[] paletteIndices = QctReader.readBytes(asyncFileChannel, byteOffset + 0x01L, size);
      return new SubPalette(size, paletteIndices);
    }
  }
}
