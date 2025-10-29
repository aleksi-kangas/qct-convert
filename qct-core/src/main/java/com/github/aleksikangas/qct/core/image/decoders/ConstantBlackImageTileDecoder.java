package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.nio.channels.AsynchronousFileChannel;

record ConstantBlackImageTileDecoder(ImageTileEncoding imageTileEncoding) implements ImageTileDecoder {
  @Override
  public ImageTileEncoding decoderFor() {
    return imageTileEncoding;
  }

  @Nonnull
  @Override
  public Color[][] decode(AsynchronousFileChannel asyncFileChannel, long byteOffset) {
    final Color[][] pixels = new Color[ImageTile.HEIGHT][ImageTile.WIDTH];
    for (int y = 0; y < ImageTile.HEIGHT; ++y) {
      for (int x = 0; x < ImageTile.WIDTH; ++x) {
        pixels[y][x] = Color.BLACK;
      }
    }
    return pixels;
  }
}
