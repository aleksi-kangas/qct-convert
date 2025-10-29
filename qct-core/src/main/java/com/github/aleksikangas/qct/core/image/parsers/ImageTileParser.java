package com.github.aleksikangas.qct.core.image.parsers;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parsers.PaletteAware;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.image.decoders.ImageTileDecoder;
import com.github.aleksikangas.qct.core.image.decoders.ImageTileDecoderFactory;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.parser.task.ParserRegistryAware;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ImageTile}.
 */
public final class ImageTileParser implements Parser<ImageTile, ImageTileParser.Task> {
  @Nonnull
  @Override
  public ImageTile execute(final Task parseTask) {
    return parseTask.parse();
  }

  @Nonnull
  @Override
  public Class<ImageTile> parseableClass() {
    return ImageTile.class;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     Palette palette,
                     ParserRegistry parserRegistry)
      implements ParseTask<ImageTile>, AsyncReadable, ByteOffsetAware, PaletteAware, ParserRegistryAware {

    @Nonnull
    @Override
    public Class<ImageTile> parseableClass() {
      return ImageTile.class;
    }

    @Nonnull
    @Override
    public ImageTile parse() {
      final ImageTileEncoding imageTileEncoding = encodingOf(asyncFileChannel, byteOffset);
      final ImageTileDecoder imageTileDecoder = ImageTileDecoderFactory.create(imageTileEncoding,
                                                                               palette,
                                                                               parserRegistry);
      final Color[][] pixels = imageTileDecoder.decode(asyncFileChannel, byteOffset);
      return new ImageTile(imageTileEncoding, pixels);
    }

    private static ImageTileEncoding encodingOf(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
      final int firstByte = QctReader.readByte(asyncFileChannel, byteOffset);
      if (firstByte == 0 || firstByte == 255) {
        return ImageTileEncoding.HUFFMAN_CODING;
      }
      if (firstByte > 127) {
        return ImageTileEncoding.PIXEL_PACKING;
      }
      return ImageTileEncoding.RUN_LENGTH_ENCODING;
    }
  }
}
