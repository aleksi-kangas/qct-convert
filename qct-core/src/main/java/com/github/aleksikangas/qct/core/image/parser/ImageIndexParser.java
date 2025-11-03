package com.github.aleksikangas.qct.core.image.parser;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parser.task.PaletteAware;
import com.github.aleksikangas.qct.core.image.ImageIndex;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.meta.parser.task.MetadataAware;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ImageIndex}.
 */
public final class ImageIndexParser extends AbstractParser<ImageIndex, ImageIndexParser.Task> {
  public ImageIndexParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<ImageIndex> parseableClass() {
    return ImageIndex.class;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     Metadata metadata,
                     Palette palette,
                     ParserRegistry parserRegistry) implements ParseTask<ImageIndex>, AsyncReadable, MetadataAware, PaletteAware, ParserRegistryAware {
    @Nonnull
    @Override
    public Class<ImageIndex> parseableClass() {
      return ImageIndex.class;
    }

    @Nonnull
    @Override
    public ImageIndex parse() {
      final ImageTile[][] imageTiles = new ImageTile[metadata.heightTiles()][metadata.widthTiles()];
      for (int y = 0; y < metadata.heightTiles(); ++y) {
        for (int x = 0; x < metadata.widthTiles(); ++x) {
          final long imageTilePointerByteOffset = ((long) metadata.widthTiles() * y + x) * 0x04L;
          final long imageTileByteOffset = QctReader.readPointer(asyncFileChannel,
                                                                 ImageIndex.BYTE_OFFSET + imageTilePointerByteOffset);
          final var task = new ImageTileParser.Task(asyncFileChannel, imageTileByteOffset, palette, parserRegistry);
          final int yTile = y;
          final int xTile = x;
          parserRegistry.parseAsync(task).thenAccept(imageTile -> imageTiles[yTile][xTile] = imageTile);
        }
      }
      return new ImageIndex(imageTiles);
    }
  }
}
