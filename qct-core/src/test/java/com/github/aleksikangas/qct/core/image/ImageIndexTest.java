package com.github.aleksikangas.qct.core.image;

import com.github.aleksikangas.qct.core.color.QctPixel;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.Arrays;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class ImageIndexTest {
  private static final QctPixel BLACK = new QctPixel(Color.BLACK, 0);
  private static final QctPixel WHITE = new QctPixel(Color.WHITE, 0);

  @Test
  void pixelTest() {
    final var imageTiles = new ImageTile[2][2];
    imageTiles[0][0] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 1, 2));
    imageTiles[0][1] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 2, 3));
    imageTiles[1][0] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 3, 4));
    imageTiles[1][1] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 4, 5));
    final var imageIndex = new ImageIndex(imageTiles);

    assertEquals(BLACK, imageIndex.pixel(0, 0));
    assertEquals(BLACK, imageIndex.pixel(127, 127));
    assertEquals(BLACK, imageIndex.pixel(13, 34));
    assertEquals(BLACK, imageIndex.pixel(78, 86));
    assertEquals(WHITE, imageIndex.pixel(1, 2));
    assertEquals(WHITE, imageIndex.pixel(2, ImageTile.WIDTH + 3));
    assertEquals(WHITE, imageIndex.pixel(ImageTile.HEIGHT + 3, 4));
    assertEquals(WHITE, imageIndex.pixel(ImageTile.HEIGHT + 4, ImageTile.WIDTH + 5));
  }

  @Test
  void asPixelsTest() {
    final var imageTiles = new ImageTile[2][2];
    imageTiles[0][0] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 1, 2));
    imageTiles[0][1] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 2, 3));
    imageTiles[1][0] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 3, 4));
    imageTiles[1][1] = new ImageTile(ImageTileEncoding.HUFFMAN_CODING, withWhitePixel(blackTilePixels(), 4, 5));
    final var imageIndex = new ImageIndex(imageTiles);

    assertEquals(BLACK, imageIndex.asPixels()[0][0]);
    assertEquals(BLACK, imageIndex.asPixels()[127][127]);
    assertEquals(BLACK, imageIndex.asPixels()[13][34]);
    assertEquals(BLACK, imageIndex.asPixels()[78][86]);
    assertEquals(WHITE, imageIndex.asPixels()[1][2]);
    assertEquals(WHITE, imageIndex.asPixels()[2][ImageTile.WIDTH + 3]);
    assertEquals(WHITE, imageIndex.asPixels()[ImageTile.HEIGHT + 3][4]);
    assertEquals(WHITE, imageIndex.asPixels()[ImageTile.HEIGHT + 4][ImageTile.WIDTH + 5]);
  }

  private static QctPixel[][] blackTilePixels() {
    final var pixels = new QctPixel[ImageTile.HEIGHT][ImageTile.WIDTH];
    IntStream.range(0, ImageTile.HEIGHT).parallel().forEach(y -> {
      final var row = new QctPixel[ImageTile.WIDTH];
      Arrays.parallelSetAll(row, _ -> BLACK);
      pixels[y] = row;
    });
    return pixels;
  }

  private static QctPixel[][] withWhitePixel(final QctPixel[][] pixels, final int yWhitePixel, final int xWhitePixel) {
    pixels[yWhitePixel][xWhitePixel] = WHITE;
    return pixels;
  }
}
