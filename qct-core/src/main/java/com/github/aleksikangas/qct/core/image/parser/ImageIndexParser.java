package com.github.aleksikangas.qct.core.image.parser;

import com.github.aleksikangas.qct.core.QctRuntimeException;
import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parser.task.PaletteAware;
import com.github.aleksikangas.qct.core.image.ImageIndex;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.meta.parser.task.MetadataAware;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ImageIndex}.
 */
@ApplicationScoped
public class ImageIndexParser extends AbstractParser<ImageIndex, ImageIndexParser.Task> {
  private final ImageTileParser imageTileParser;

  @Inject
  public ImageIndexParser(final ImageTileParser imageTileParser) {
    this.imageTileParser = Objects.requireNonNull(imageTileParser);
  }

  @Nonnull
  @Override
  public Class<ImageIndex> parseableClass() {
    return ImageIndex.class;
  }

  @Nonnull
  @Override
  public ImageIndex parse(final Task parseTask) {
    final ImageTile[][] imageTiles = new ImageTile[parseTask.metadata.heightTiles()][parseTask.metadata.widthTiles()];
    final List<CompletableFuture<?>> imageTileFutures = new ArrayList<>();
    for (int y = 0; y < parseTask.metadata.heightTiles(); ++y) {
      for (int x = 0; x < parseTask.metadata.widthTiles(); ++x) {
        final long imageTilePointerByteOffset = ((long) parseTask.metadata.widthTiles() * y + x) * 0x04L;
        final long imageTileByteOffset = QctReader.readPointer(parseTask.asyncFileChannel,
                                                               ImageIndex.BYTE_OFFSET + imageTilePointerByteOffset);
        final var task = new ImageTileParser.Task(parseTask.asyncFileChannel,
                                                  imageTileByteOffset,
                                                  y,
                                                  x,
                                                  parseTask.palette);
        final int yTile = y;
        final int xTile = x;
        imageTileFutures.add(imageTileParser.parseAsync(task).thenAccept(imageTile -> imageTiles[yTile][xTile] = imageTile));
      }
    }
    imageTileFutures.forEach(imageTileFuture -> {
      try {
        imageTileFuture.get();
      } catch (final ExecutionException e) {
        throw new QctRuntimeException(e);
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new QctRuntimeException(e);
      }
    });
    return new ImageIndex(imageTiles);
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     Metadata metadata,
                     Palette palette) implements ParseTask, AsyncFileChannelAware, MetadataAware, PaletteAware {
  }
}
