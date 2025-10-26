package com.github.aleksikangas.qct.core.color.parsers;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.awt.*;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link Palette}.
 */
public final class PaletteParser implements Parser<Palette, PaletteParser.Task> {
  @Nonnull
  @Override
  public Class<Palette> parseableClass() {
    return Palette.class;
  }

  @Nonnull
  @Override
  public Palette execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel) implements ParseTask<Palette>, AsyncReadable {

    @Nonnull
    @Override
    public Class<Palette> parseableClass() {
      return Palette.class;
    }

    @Nonnull
    @Override
    public Palette parse() {
      final int[] bytes = QctReader.readBytes(asyncFileChannel, Palette.BYTE_OFFSET, Palette.SIZE * 4);
      final Color[] colors = new Color[Palette.SIZE];
      for (int i = 0; i < Palette.SIZE; ++i) {
        final int blue = bytes[i * 4];
        final int green = bytes[i * 4 + 1];
        final int red = bytes[i * 4 + 2];
        colors[i] = new Color(red, green, blue);
      }
      return new Palette(colors);
    }
  }
}
