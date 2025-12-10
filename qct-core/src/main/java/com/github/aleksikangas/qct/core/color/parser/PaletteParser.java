package com.github.aleksikangas.qct.core.color.parser;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link Palette}.
 */
@ApplicationScoped
public class PaletteParser extends AbstractParser<Palette, PaletteParser.Task> {
  @Nonnull
  @Override
  public Class<Palette> parseableClass() {
    return Palette.class;
  }

  @Nonnull
  @Override
  public Palette parse(Task parseTask) {
    final int[] bytes = QctReader.readBytes(parseTask.asyncFileChannel, Palette.BYTE_OFFSET, Palette.SIZE * 4);
    final Color[] colors = new Color[Palette.SIZE];
    for (int i = 0; i < Palette.SIZE; ++i) {
      final int blue = bytes[i * 4];
      final int green = bytes[i * 4 + 1];
      final int red = bytes[i * 4 + 2];
      colors[i] = new Color(red, green, blue);
    }
    return new Palette(colors);
  }

  public record Task(AsynchronousFileChannel asyncFileChannel) implements ParseTask<Palette>, AsyncFileChannelAware {
  }
}
