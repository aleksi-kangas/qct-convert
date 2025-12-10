package com.github.aleksikangas.qct.core.image.parser;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.QctPixel;
import com.github.aleksikangas.qct.core.color.parser.task.PaletteAware;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.image.decoders.ImageTileDecoder;
import com.github.aleksikangas.qct.core.image.decoders.ImageTileDecoderFactory;
import com.github.aleksikangas.qct.core.image.decoders.QctDecoderException;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.feature.ByteOffsetAware;
import com.github.aleksikangas.qct.core.parser.task.CoordinatesAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link ImageTile}.
 */
@ApplicationScoped
public class ImageTileParser extends AbstractParser<ImageTile, ImageTileParser.Task> {
  private static final Logger LOG = LoggerFactory.getLogger(ImageTileParser.class);

  @Nonnull
  @Override
  public Class<ImageTile> parseableClass() {
    return ImageTile.class;
  }

  @Nonnull
  @Override
  public ImageTile parse(final Task parseTask) {
    final ImageTileEncoding imageTileEncoding = encodingOf(parseTask.asyncFileChannel, parseTask.byteOffset);
    final ImageTileDecoder imageTileDecoder = ImageTileDecoderFactory.create(imageTileEncoding,
                                                                             parseTask.palette);
    try {
      final QctPixel[][] pixels = imageTileDecoder.decode(parseTask.asyncFileChannel, parseTask.byteOffset);
      return new ImageTile(imageTileEncoding, pixels);
    } catch (final QctDecoderException e) {
      LOG.warn("Failed to decode ImageTile=[y={}, x={}], replacing with palette first color (R={}, G={}, B={}) pixels",
               parseTask.y,
               parseTask.x,
               parseTask.palette.getColor(0).getRed(),
               parseTask.palette.getColor(0).getGreen(),
               parseTask.palette.getColor(0).getBlue(),
               e);
      return new ImageTile(imageTileEncoding, allPaletteFirstColorPixels(parseTask.palette));
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

  private static QctPixel[][] allPaletteFirstColorPixels(final Palette palette) {
    final var pixels = new QctPixel[ImageTile.HEIGHT][ImageTile.WIDTH];
    for (int y = 0; y < ImageTile.HEIGHT; ++y) {
      for (int x = 0; x < ImageTile.WIDTH; ++x) {
        pixels[y][x] = new QctPixel(palette.getColor(0), 0);
      }
    }
    return pixels;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     int y,
                     int x,
                     Palette palette) implements ParseTask<ImageTile>, AsyncFileChannelAware, ByteOffsetAware, CoordinatesAware, PaletteAware {
  }
}
