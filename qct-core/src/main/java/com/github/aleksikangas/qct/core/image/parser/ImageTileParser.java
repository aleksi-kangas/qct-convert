package com.github.aleksikangas.qct.core.image.parser;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.parser.task.PaletteAware;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.image.decoders.ImageTileDecoder;
import com.github.aleksikangas.qct.core.image.decoders.ImageTileDecoderFactory;
import com.github.aleksikangas.qct.core.image.decoders.QctDecoderException;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.task.*;
import com.github.aleksikangas.qct.core.reader.QctReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ImageTile}.
 */
public final class ImageTileParser extends AbstractParser<ImageTile, ImageTileParser.Task> {
  private static final Logger LOG = LoggerFactory.getLogger(ImageTileParser.class);

  public ImageTileParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<ImageTile> parseableClass() {
    return ImageTile.class;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     int y,
                     int x,
                     Palette palette,
                     ParserRegistry parserRegistry) implements ParseTask<ImageTile>, AsyncReadable, ByteOffsetAware, CoordinatesAware, PaletteAware, ParserRegistryAware {

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
      try {
        final Color[][] pixels = imageTileDecoder.decode(asyncFileChannel, byteOffset);
        return new ImageTile(imageTileEncoding, pixels);
      } catch (final QctDecoderException e) {
        LOG.warn("Failed to decode ImageTile=[y={}, x={}], replacing with black pixels", y, x, e);
        return new ImageTile(imageTileEncoding, allBlackTilePixels());
      }
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

    private static Color[][] allBlackTilePixels() {
      final Color[][] pixels = new Color[ImageTile.HEIGHT][ImageTile.WIDTH];
      for (int y = 0; y < ImageTile.HEIGHT; ++y) {
        for (int x = 0; x < ImageTile.WIDTH; ++x) {
          pixels[y][x] = Color.BLACK;
        }
      }
      return pixels;
    }
  }
}
