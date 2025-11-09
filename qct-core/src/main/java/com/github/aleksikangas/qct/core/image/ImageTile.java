package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.color.QctPixel;
import com.github.aleksikangas.qct.core.parser.Parseable;

/**
 * Represents a single tile of an image. Each tile within the image is compressed to reduce the size of the image file.
 * Three main compression algorithms ({@link ImageTileEncoding}) are used to compress the image data, and all three
 * algorithms rely on the fact that an image tile will likely contain fewer colors than the overall image.
 */
public record ImageTile(ImageTileEncoding encoding,
                        QctPixel[][] pixels) implements Parseable<ImageTile> {
  public static final int HEIGHT = 64;
  public static final int WIDTH = 64;
  public static final int PIXEL_COUNT = HEIGHT * WIDTH;

  public QctPixel pixel(final int y, final int x) {
    return pixels[y][x];
  }
}
