package com.github.aleksikangas.qct.core.image.util;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.color.Palette;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public final class ImageIndexUtils {
  /**
   * Transform the given {@link QctFile} to 8-bit RGBA pixels, packed as integers (bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue).
   *
   * @param qctFile to transform
   * @return 8-bit RGBA pixels
   */
  public static int[][] asPixels(final QctFile qctFile) {
    final int[][] paletteIndices = qctFile.imageIndex().asPaletteIndices();
    final int[][] pixels = new int[qctFile.height()][qctFile.width()];
    IntStream.range(0, qctFile.height())
             .parallel()
             .forEach(y -> {
               final var rowPixels = new int[qctFile.width()];
               Arrays.parallelSetAll(rowPixels, x -> {
                 final int paletteIndex = paletteIndices[y][x];
                 return qctFile.palette().getRGBA(paletteIndex);
               });
               paletteIndices[y] = rowPixels;
             });
    return pixels;
  }

  /**
   * Transform the given {@link QctFile} as a {@link BufferedImage}.
   *
   * @param qctFile to transform
   * @return transformed {@link BufferedImage}
   */
  public static BufferedImage asBufferedImage(final QctFile qctFile) {
    final var indexColorModel = new IndexColorModel(8,
                                                    Palette.SIZE,
                                                    qctFile.palette().redBytes(),
                                                    qctFile.palette().greenBytes(),
                                                    qctFile.palette().blueBytes(),
                                                    null);
    final var bufferedImage = new BufferedImage(qctFile.width(),
                                                qctFile.height(),
                                                BufferedImage.TYPE_BYTE_INDEXED,
                                                indexColorModel);
    final WritableRaster writableRaster = bufferedImage.getRaster();
    writableRaster.setDataElements(0,
                                   0,
                                   qctFile.width(),
                                   qctFile.height(),
                                   qctFile.imageIndex().asFlatBytePaletteIndices());
    return bufferedImage;
  }

  public enum Channel {
    RED,
    GREEN,
    BLUE
  }

  public static int[] channelValues(final QctFile qctFile, final Channel channel) {
    return switch (channel) {
      case RED -> extractChannelValues(qctFile, paletteIndex -> qctFile.palette().getColor(paletteIndex).getRed());
      case GREEN -> extractChannelValues(qctFile, paletteIndex -> qctFile.palette().getColor(paletteIndex).getGreen());
      case BLUE -> extractChannelValues(qctFile, paletteIndex -> qctFile.palette().getColor(paletteIndex).getBlue());
    };
  }

  private static int[] extractChannelValues(final QctFile qctFile, final ToIntFunction<Integer> channelExtractor) {
    final var channelValues = new int[qctFile.pixelCount()];
    IntStream.range(0, qctFile.height())
             .parallel()
             .forEach(y -> {
               final var rowValues = new int[qctFile.width()];
               Arrays.parallelSetAll(rowValues, x -> {
                 final int paletteIndex = qctFile.imageIndex().paletteIndexOfPixel(y, x);
                 return channelExtractor.applyAsInt(paletteIndex);
               });
               System.arraycopy(rowValues, 0, channelValues, y * qctFile.width(), rowValues.length);
             });
    return channelValues;
  }

  private ImageIndexUtils() {
  }
}
