package com.github.aleksikangas.qct.core.image.util;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.image.ImageTile;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.IntStream;

public final class ImageTileUtils {
  /**
   * Transform the given {@link ImageTile} as a {@link BufferedImage}, using the given {@link Palette}.
   *
   * @param imageTile to transform
   * @param palette   to use
   * @return transformed {@link BufferedImage}
   */
  public static BufferedImage asBufferedImage(final ImageTile imageTile, final Palette palette) {
    final var bufferedImage = new BufferedImage(ImageTile.WIDTH, ImageTile.HEIGHT, BufferedImage.TYPE_INT_ARGB);
    IntStream.range(0, ImageTile.HEIGHT)
             .parallel()
             .forEach(y -> {
               final int[] rowPixels = new int[ImageTile.WIDTH];
               Arrays.parallelSetAll(rowPixels, x -> {
                 final int paletteIndex = imageTile.paletteIndices()[y][x];
                 return palette.getRGBA(paletteIndex);
               });
               bufferedImage.setRGB(0, y, ImageTile.WIDTH, 1, rowPixels, 0, ImageTile.WIDTH);
             });
    return bufferedImage;
  }

  private ImageTileUtils() {
  }
}
