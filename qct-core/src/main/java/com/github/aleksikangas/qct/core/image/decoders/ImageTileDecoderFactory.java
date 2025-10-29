package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A factory for {@link ImageTileDecoder}s.
 */
public final class ImageTileDecoderFactory {
  public static ImageTileDecoder create(final ImageTileEncoding imageTileEncoding,
                                        final Palette palette,
                                        final ParserRegistry parserRegistry) {
    return switch (imageTileEncoding) {
      case HUFFMAN_CODING -> new PlaceholderImageTileDecoder(ImageTileEncoding.HUFFMAN_CODING);
      case PIXEL_PACKING -> new PlaceholderImageTileDecoder(ImageTileEncoding.PIXEL_PACKING);
      case RUN_LENGTH_ENCODING -> new RleImageTileDecoder(palette, parserRegistry);
    };
  }

  private record PlaceholderImageTileDecoder(ImageTileEncoding imageTileEncoding) implements ImageTileDecoder {

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
}
