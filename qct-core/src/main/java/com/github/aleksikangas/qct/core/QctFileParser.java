package com.github.aleksikangas.qct.core;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parsers.InterpolationMatrixParser;
import com.github.aleksikangas.qct.core.color.parsers.PaletteParser;
import com.github.aleksikangas.qct.core.georef.parsers.GeoreferencingCoefficientsParser;
import com.github.aleksikangas.qct.core.image.parsers.ImageIndexParser;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.meta.parsers.MetadataParser;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

public final class QctFileParser implements Parser<QctFile, QctFileParser.Task> {
  @Nonnull
  @Override
  public Class<QctFile> parseableClass() {
    return QctFile.class;
  }

  @Nonnull
  @Override
  public QctFile execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     ParserRegistry parserRegistry) implements ParseTask<QctFile>, AsyncReadable, ParserRegistryAware {
    @Nonnull
    @Override
    public Class<QctFile> parseableClass() {
      return QctFile.class;
    }

    @Nonnull
    @Override
    public QctFile parse() {
      final Metadata metadata = parserRegistry.parse(new MetadataParser.Task(asyncFileChannel, parserRegistry));
      final Palette palette = parserRegistry.parse(new PaletteParser.Task(asyncFileChannel));
      return new QctFile(metadata,
                         parserRegistry.parse(new GeoreferencingCoefficientsParser.Task(asyncFileChannel)),
                         palette,
                         parserRegistry.parse(new InterpolationMatrixParser.Task(asyncFileChannel)),
                         parserRegistry.parse(new ImageIndexParser.Task(asyncFileChannel,
                                                                        metadata,
                                                                        palette,
                                                                        parserRegistry)));
    }
  }
}
