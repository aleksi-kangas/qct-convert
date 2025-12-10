package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.QctPixel;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.image.color.SubPalette;
import com.github.aleksikangas.qct.core.image.color.SubPaletteSizeType;
import com.github.aleksikangas.qct.core.image.parser.SubPaletteParser;
import com.github.aleksikangas.qct.core.parser.Parsers;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.Objects;

/**
 * An {@link ImageTileDecoder} for {@link ImageTile}s using {@link ImageTileEncoding#RUN_LENGTH_ENCODING}.
 */
final class RleImageTileDecoder extends AbstractImageTileDecoder {
  private final Palette palette;

  RleImageTileDecoder(final Palette palette) {
    this.palette = Objects.requireNonNull(palette);
  }

  @Override
  public ImageTileEncoding decoderFor() {
    return ImageTileEncoding.RUN_LENGTH_ENCODING;
  }

  @Nonnull
  @Override
  protected QctPixel[][] decodePixels(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
    final QctPixel[][] pixels = new QctPixel[ImageTile.HEIGHT][ImageTile.WIDTH];
    final SubPalette subPalette = Parsers.execute(SubPaletteParser.class, new SubPaletteParser.Task(asyncFileChannel,
                                                                                                    byteOffset,
                                                                                                    SubPaletteSizeType.NORMAL));
    final long pixelDataByteOffset = byteOffset + 0x01L + subPalette.size();
    // In order to avoid reading one byte at a time from the file,
    // read bytes into a buffer assuming the worst case of one byte per pixel (64 x 64 = 4096 bytes),
    // which practically should never occur.
    final int[] bytes = QctReader.readBytesSafe(asyncFileChannel, pixelDataByteOffset, ImageTile.PIXEL_COUNT);
    int byteIndex = 0;
    int pixelCount = 0;
    while (pixelCount < ImageTile.PIXEL_COUNT) {
      final int rleByte = bytes[byteIndex++];
      final DecodedRleByte decodedRleByte = decodeRleByte(rleByte, subPalette);
      final var color = new QctPixel(palette.colors()[decodedRleByte.paletteIndex], decodedRleByte.paletteIndex);
      for (int i = 0; i < decodedRleByte.runLength; ++i) {
        final int y = (pixelCount + i) / ImageTile.WIDTH;
        final int x = (pixelCount + i) % ImageTile.WIDTH;
        pixels[y][x] = color;
      }
      pixelCount += decodedRleByte.runLength;
    }
    return pixels;
  }

  private static DecodedRleByte decodeRleByte(final int rleByte, final SubPalette subPalette) {
    final int subPaletteIndexMask = (1 << subPalette.bitsRequiredToIndex()) - 1;
    final int subPaletteIndex = rleByte & subPaletteIndexMask;
    final int rumLength = rleByte >> subPalette.bitsRequiredToIndex();
    return new DecodedRleByte(subPalette.paletteIndexOf(subPaletteIndex), rumLength);
  }

  private record DecodedRleByte(int paletteIndex,
                                int runLength) {
  }
}
