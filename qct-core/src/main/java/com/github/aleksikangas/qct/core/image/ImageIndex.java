package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.color.QctPixel;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.google.common.base.Preconditions;

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

  public ImageTile imageTile(final int yTile, final int xTile) {
    Preconditions.checkArgument(0 <= yTile && yTile < heightTiles());
    Preconditions.checkArgument(0 <= xTile && xTile < widthTiles());
    return imageTiles[yTile][xTile];
  }

  public QctPixel pixel(final int y, final int x) {
    Preconditions.checkArgument(0 <= y && y < height());
    Preconditions.checkArgument(0 <= x && x < width());
    final ImageTile imageTile = imageTiles[y / ImageTile.HEIGHT][x / ImageTile.WIDTH];
    return imageTile.pixel(y % ImageTile.HEIGHT, x % ImageTile.WIDTH);
  }

  public QctPixel[][] asPixels() {
    final var pixels = new QctPixel[height()][width()];
    IntStream.range(0, height())
        .parallel()
        .forEach(y -> {
          final var rowPixels = new QctPixel[width()];
          Arrays.parallelSetAll(rowPixels, x -> {
            final ImageTile imageTile = imageTile(y / ImageTile.HEIGHT, x / ImageTile.WIDTH);
            return imageTile.pixel(y % ImageTile.HEIGHT, x % ImageTile.WIDTH);
          });
          pixels[y] = rowPixels;
        });
    return pixels;
  }
}
