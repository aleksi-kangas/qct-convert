package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.color.QctPixel;
import com.google.common.base.Preconditions;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Various utility function related to the image.
 */
public final class ImageUtils {
  public static BufferedImage asBufferedImage(final QctPixel[][] pixels) {
    Preconditions.checkArgument(pixels.length > 0 && pixels[0].length > 0);
    final int height = pixels.length;
    final int width = pixels[0].length;
    final var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    IntStream.range(0, height).parallel().forEach(y -> {
      final QctPixel[] rowPixels = pixels[y];
      final int[] rowRgb = new int[width];
      Arrays.parallelSetAll(rowRgb, x -> {
        final Color c = rowPixels[x].color();
        return (c.getAlpha() << 24) | (c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue();
      });
      bufferedImage.setRGB(0, y, width, 1, rowRgb, 0, width);
    });
    return bufferedImage;
  }

  private ImageUtils() {
  }
}
