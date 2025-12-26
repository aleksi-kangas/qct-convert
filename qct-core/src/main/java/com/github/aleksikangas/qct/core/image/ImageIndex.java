package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.parser.Parseable;
import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * <pre>
 * +--------+-------------------+--------------------------------------------+
 * | Offset | Size (Bytes)      | Content                                    |
 * +--------+-------------------+--------------------------------------------+
 * | 0x45A0 | w x h x 4         | Image Index Pointers - QC3 files omit this |
 * +--------+-------------------+--------------------------------------------+
 * </pre>
 */
public record ImageIndex(ImageTile[][] imageTiles) implements Parseable {
  public static final long BYTE_OFFSET = 0x45A0L;

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final ImageIndex that = (ImageIndex) o;
    return Objects.deepEquals(imageTiles, that.imageTiles);
  }

  @Override
  public int hashCode() {
    return Arrays.deepHashCode(imageTiles);
  }

  @Nonnull
  @Override
  public String toString() {
    return "ImageIndex{" +
        "imageTiles=" + Arrays.toString(imageTiles) +
        '}';
  }

  public int heightTiles() {
    return imageTiles.length;
  }

  public int widthTiles() {
    return imageTiles[0].length;
  }

  public int height() {
    return heightTiles() * ImageTile.HEIGHT;
  }

  public int width() {
    return widthTiles() * ImageTile.WIDTH;
  }

  public int pixelCount() {
    return Math.multiplyExact(height(), width());
  }

  public ImageTile imageTile(final int yTile, final int xTile) {
    Objects.checkIndex(yTile, heightTiles());
    Objects.checkIndex(xTile, widthTiles());
    return imageTiles[yTile][xTile];
  }

  public ImageTile imageTileOfPixel(final int yPixel, final int xPixel) {
    Objects.checkIndex(yPixel, height());
    Objects.checkIndex(xPixel, width());
    return imageTiles[yPixel / ImageTile.HEIGHT][xPixel / ImageTile.WIDTH];
  }

  public int paletteIndexOfPixel(final int yPixel, final int xPixel) {
    return imageTileOfPixel(yPixel, xPixel).paletteIndices()[yPixel % ImageTile.HEIGHT][xPixel % ImageTile.WIDTH];
  }

  public int[][] asPaletteIndices() {
    final var paletteIndices = new int[height()][width()];
    IntStream.range(0, height())
             .parallel()
             .forEach(y -> paletteIndices[y] = rowPaletteIndices(y));
    return paletteIndices;
  }

  public byte[] asFlatBytePaletteIndices() {
    final var bytePaletteIndices = new byte[pixelCount()];
    IntStream.range(0, pixelCount())
             .parallel()
             .forEach(i -> {
               final int y = i / width();
               final int x = i % width();
               bytePaletteIndices[i] = (byte) paletteIndexOfPixel(y, x);
             });
    return bytePaletteIndices;
  }

  public int[] rowPaletteIndices(final int y) {
    final var rowPaletteIndices = new int[width()];
    IntStream.range(0, width())
             .parallel()
             .forEach(x -> rowPaletteIndices[x] = paletteIndexOfPixel(y, x));
    return rowPaletteIndices;
  }
}
