package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A factory for {@link ImageTileDecoder}s.
 */
public final class ImageTileDecoderFactory {
  public static ImageTileDecoder create(final ImageTileEncoding imageTileEncoding) {
    return switch (imageTileEncoding) {
      case HUFFMAN_CODING -> new HuffmanImageTileDecoder();
      case PIXEL_PACKING -> new PlaceholderImageTileDecoder(ImageTileEncoding.PIXEL_PACKING);
      case RUN_LENGTH_ENCODING -> new RleImageTileDecoder();
    };
  }

  private record PlaceholderImageTileDecoder(ImageTileEncoding imageTileEncoding) implements ImageTileDecoder {

    @Override
    public ImageTileEncoding decoderFor() {
      return imageTileEncoding;
    }

    @Nonnull
    @Override
    public int[][] decode(AsynchronousFileChannel asyncFileChannel, long byteOffset) {
      return new int[ImageTile.HEIGHT][ImageTile.WIDTH];
    }
  }
}
