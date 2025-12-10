package com.github.aleksikangas.qct.core;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parser.InterpolationMatrixParser;
import com.github.aleksikangas.qct.core.color.parser.PaletteParser;
import com.github.aleksikangas.qct.core.georef.parser.GeoreferencingCoefficientsParser;
import com.github.aleksikangas.qct.core.image.parser.ImageIndexParser;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.meta.parser.MetadataParser;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parsers;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link QctFile}.
 */
@ApplicationScoped
public class QctFileParser extends AbstractParser<QctFile, QctFileParser.Task> {
  @Nonnull
  @Override
  public Class<QctFile> parseableClass() {
    return QctFile.class;
  }

  @Nonnull
  @Override
  public QctFile parse(final Task parseTask) {
    final Metadata metadata = Parsers.execute(MetadataParser.class,
                                              new MetadataParser.Task(parseTask.asyncFileChannel));
    final Palette palette = Parsers.execute(PaletteParser.class, new PaletteParser.Task(parseTask.asyncFileChannel));
    return new QctFile(metadata,
                       Parsers.execute(GeoreferencingCoefficientsParser.class,
                                       new GeoreferencingCoefficientsParser.Task(parseTask.asyncFileChannel)),
                       palette,
                       Parsers.execute(InterpolationMatrixParser.class,
                                       new InterpolationMatrixParser.Task(parseTask.asyncFileChannel)),
                       Parsers.execute(ImageIndexParser.class,
                                       new ImageIndexParser.Task(parseTask.asyncFileChannel, metadata, palette)));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel) implements ParseTask<QctFile>, AsyncFileChannelAware {
  }
}
