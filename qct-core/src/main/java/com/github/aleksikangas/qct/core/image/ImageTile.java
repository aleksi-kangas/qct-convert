package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a single tile of an image. Each tile within the image is compressed to reduce the size of the image file.
 * Three main compression algorithms ({@link ImageTileEncoding}) are used to compress the image data, and all three
 * algorithms rely on the fact that an image tile will likely contain fewer colors than the overall image.
 *
 * @param encoding       {@link ImageTileEncoding} of the image tile
 * @param paletteIndices palette indices of each pixel
 */
public record ImageTile(ImageTileEncoding encoding,
                        int[][] paletteIndices) implements Parseable {
  public static final int HEIGHT = 64;
  public static final int WIDTH = 64;
  public static final int PIXEL_COUNT = HEIGHT * WIDTH;

  public ImageTile {
    Preconditions.checkArgument(paletteIndices.length == ImageTile.HEIGHT && paletteIndices[0].length == ImageTile.WIDTH);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ImageTile imageTile = (ImageTile) o;
    return Objects.deepEquals(paletteIndices, imageTile.paletteIndices) && encoding == imageTile.encoding;
  }

  @Override
  public int hashCode() {
    return Objects.hash(encoding, Arrays.deepHashCode(paletteIndices));
  }

  @Nonnull
  @Override
  public String toString() {
    return "ImageTile{" +
        "encoding=" + encoding +
        ", paletteIndices=" + Arrays.toString(paletteIndices) +
        '}';
  }

  /**
   * Returns the {@link com.github.aleksikangas.qct.core.color.Palette} index of the pixel color value at the given location.
   *
   * @param y y-coordinate, in tile coordinate space
   * @param x x-coordinate, in tile coordinate space
   * @return the {@link com.github.aleksikangas.qct.core.color.Palette} index of the pixel color value
   */
  public int pixelPaletteIndex(final int y, final int x) {
    return paletteIndices[y][x];
  }
}
