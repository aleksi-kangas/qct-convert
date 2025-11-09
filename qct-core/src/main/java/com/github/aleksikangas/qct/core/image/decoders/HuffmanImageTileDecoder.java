package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.color.QctPixel;
import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.reader.DynamicByteBuffer;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.Objects;

final class HuffmanImageTileDecoder extends AbstractImageTileDecoder {
  private final Palette palette;

  public HuffmanImageTileDecoder(final Palette palette) {
    this.palette = Objects.requireNonNull(palette);
  }

  @Override
  public ImageTileEncoding decoderFor() {
    return ImageTileEncoding.HUFFMAN_CODING;
  }

  @Nonnull
  @Override
  protected QctPixel[][] decodePixels(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) throws QctDecoderException {
    final DynamicByteBuffer dynamicByteBuffer = new DynamicByteBuffer(asyncFileChannel, byteOffset + 1, 4096);
    final HuffmanCodeBook huffmanCodeBook = new HuffmanCodeBook(dynamicByteBuffer);
    if (huffmanCodeBook.size() == 1) {
      return decodeConstant(huffmanCodeBook);
    }
    return decodeWith(huffmanCodeBook, dynamicByteBuffer);
  }

  private QctPixel[][] decodeConstant(final HuffmanCodeBook huffmanCodeBook) {
    Preconditions.checkArgument(huffmanCodeBook.size() == 1);
    final var pixels = new QctPixel[ImageTile.HEIGHT][ImageTile.WIDTH];
    final QctPixel color = huffmanCodeBook.getColor(0, palette);
    for (int y = 0; y < ImageTile.HEIGHT; ++y) {
      for (int x = 0; x < ImageTile.WIDTH; ++x) {
        pixels[y][x] = color;
      }
    }
    return pixels;
  }

  private QctPixel[][] decodeWith(final HuffmanCodeBook huffmanCodeBook, final DynamicByteBuffer dynamicByteBuffer) {
    Preconditions.checkArgument(huffmanCodeBook.size() > 1);
    final var pixels = new QctPixel[ImageTile.HEIGHT][ImageTile.WIDTH];
    int currentByte = dynamicByteBuffer.nextByte();
    int bitCount = 8;
    int pixelIndex = 0;
    while (pixelIndex < ImageTile.PIXEL_COUNT) {
      if (huffmanCodeBook.isColor()) {
        final int y = pixelIndex / ImageTile.WIDTH;
        final int x = pixelIndex % ImageTile.WIDTH;
        pixels[y][x] = huffmanCodeBook.getColor(palette);
        ++pixelIndex;
        huffmanCodeBook.reset();
        continue;
      }
      final boolean bit = (currentByte & 1) == 1;
      huffmanCodeBook.step(bit);
      currentByte >>= 1;
      --bitCount;
      if (bitCount == 0) {
        currentByte = dynamicByteBuffer.nextByte();
        bitCount = 8;
      }
    }
    return pixels;
  }
}
