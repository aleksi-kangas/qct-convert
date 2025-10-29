package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.parser.Parseable;

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
    return imageTiles[yTile][xTile];
  }
}
