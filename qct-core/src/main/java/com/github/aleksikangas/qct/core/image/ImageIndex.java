package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.google.common.base.Preconditions;

import java.awt.image.BufferedImage;

/**
 * <pre>
 * +--------+-------------------+--------------------------------------------+
 * | Offset | Size (Bytes)      | Content                                    |
 * +--------+-------------------+--------------------------------------------+
 * | 0x45A0 | w x h x 4         | Image Index Pointers - QC3 files omit this |
 * +--------+-------------------+--------------------------------------------+
 * </pre>
 */
public record ImageIndex(ImageTile[][] imageTiles) implements Parseable<ImageIndex> {
  public static final long BYTE_OFFSET = 0x45A0L;

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

  public BufferedImage asBufferedImage() {
    final BufferedImage bufferedImage = new BufferedImage(width(), height(), BufferedImage.TYPE_INT_RGB);
    for (int yTile = 0; yTile < heightTiles(); ++yTile) {
      for (int xTile = 0; xTile < widthTiles(); ++xTile) {
        final ImageTile imageTile = imageTiles[yTile][xTile];
        for (int y = 0; y < ImageTile.HEIGHT; ++y) {
          for (int x = 0; x < ImageTile.WIDTH; ++x) {
            bufferedImage.setRGB(xTile * ImageTile.WIDTH + x,
                                 yTile * ImageTile.HEIGHT + y,
                                 imageTile.pixel(y, x).getRGB());
          }
        }
      }
    }
    return bufferedImage;
  }
}
