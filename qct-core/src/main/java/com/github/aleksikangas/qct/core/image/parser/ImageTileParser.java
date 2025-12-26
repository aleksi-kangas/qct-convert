package com.github.aleksikangas.qct.core.image.parser;

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
    final ImageTileDecoder imageTileDecoder = ImageTileDecoderFactory.create(imageTileEncoding);
    try {
      final int[][] paletteIndices = imageTileDecoder.decode(parseTask.asyncFileChannel, parseTask.byteOffset);
      return new ImageTile(imageTileEncoding, paletteIndices);
    } catch (final QctDecoderException e) {
      LOG.warn("Failed to decode ImageTile=[y={}, x={}], replacing with palette first color index",
               parseTask.y,
               parseTask.x,
               e);
      return new ImageTile(imageTileEncoding, new int[ImageTile.HEIGHT][ImageTile.WIDTH]);
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

  public record Task(AsynchronousFileChannel asyncFileChannel,
                     long byteOffset,
                     int y,
                     int x) implements ParseTask, AsyncFileChannelAware, ByteOffsetAware, CoordinatesAware {
  }
}
