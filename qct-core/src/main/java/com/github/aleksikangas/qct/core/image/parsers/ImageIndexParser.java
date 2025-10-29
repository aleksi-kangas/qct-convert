package com.github.aleksikangas.qct.core.image.parsers;

import com.github.aleksikangas.qct.core.QctRuntimeException;
import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parsers.PaletteAware;
import com.github.aleksikangas.qct.core.image.ImageIndex;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.meta.parsers.MetadataAware;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ImageIndex}.
 */
public final class ImageIndexParser implements Parser<ImageIndex, ImageIndexParser.Task> {
  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  @Nonnull
  @Override
  public Class<ImageIndex> parseableClass() {
    return ImageIndex.class;
  }

  @Nonnull
  @Override
  public ImageIndex execute(final Task parseTask) {
    try {
      return executorService.submit(parseTask::parse).get();
    } catch (final ExecutionException e) {
      throw new QctRuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctRuntimeException(e);
    }
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
          imageTiles[y][x] = parserRegistry.parse(new ImageTileParser.Task(asyncFileChannel,
                                                                           imageTileByteOffset,
                                                                           palette,
                                                                           parserRegistry));
        }
      }
      return new ImageIndex(imageTiles);
    }
  }
}
