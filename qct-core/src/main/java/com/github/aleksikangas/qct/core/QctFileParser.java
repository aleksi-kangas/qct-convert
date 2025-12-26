package com.github.aleksikangas.qct.core;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parser.InterpolationMatrixParser;
import com.github.aleksikangas.qct.core.color.parser.PaletteParser;
import com.github.aleksikangas.qct.core.georef.parser.GeoreferencingCoefficientsParser;
import com.github.aleksikangas.qct.core.image.parser.ImageIndexParser;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.meta.parser.MetadataParser;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.PathAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link QctFile}.
 */
@ApplicationScoped
public class QctFileParser extends AbstractParser<QctFile, QctFileParser.Task> {
  private final MetadataParser metadataParser;
  private final PaletteParser paletteParser;
  private final GeoreferencingCoefficientsParser georeferencingCoefficientsParser;
  private final InterpolationMatrixParser interpolationMatrixParser;
  private final ImageIndexParser imageIndexParser;

  @Inject
  public QctFileParser(final MetadataParser metadataParser,
                       final PaletteParser paletteParser,
                       final GeoreferencingCoefficientsParser georeferencingCoefficientsParser,
                       final InterpolationMatrixParser interpolationMatrixParser,
                       final ImageIndexParser imageIndexParser) {
    this.metadataParser = Objects.requireNonNull(metadataParser);
    this.paletteParser = Objects.requireNonNull(paletteParser);
    this.georeferencingCoefficientsParser = Objects.requireNonNull(georeferencingCoefficientsParser);
    this.interpolationMatrixParser = Objects.requireNonNull(interpolationMatrixParser);
    this.imageIndexParser = Objects.requireNonNull(imageIndexParser);
  }


  @Nonnull
  @Override
  public Class<QctFile> parseableClass() {
    return QctFile.class;
  }

  @Nonnull
  @Override
  public QctFile parse(final Task parseTask) {
    final Metadata metadata = metadataParser.parse(new MetadataParser.Task(parseTask.asyncFileChannel));
    final Palette palette = paletteParser.parse(new PaletteParser.Task(parseTask.asyncFileChannel));
    return new QctFile(parseTask.path,
                       metadata,
                       georeferencingCoefficientsParser.parse(new GeoreferencingCoefficientsParser.Task(parseTask.asyncFileChannel)),
                       palette,
                       interpolationMatrixParser.parse(new InterpolationMatrixParser.Task(parseTask.asyncFileChannel)),
                       imageIndexParser.parse(new ImageIndexParser.Task(parseTask.asyncFileChannel, metadata)));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     Path path) implements ParseTask, AsyncFileChannelAware, PathAware {
  }
}
