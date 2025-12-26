package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.reader.DynamicByteBuffer;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

final class HuffmanImageTileDecoder extends AbstractImageTileDecoder {
  @Override
  public ImageTileEncoding decoderFor() {
    return ImageTileEncoding.HUFFMAN_CODING;
  }

  @Nonnull
  @Override
  protected int[][] decodePaletteIndices(final AsynchronousFileChannel asyncFileChannel,
                                         final long byteOffset) throws QctDecoderException {
    final DynamicByteBuffer dynamicByteBuffer = new DynamicByteBuffer(asyncFileChannel, byteOffset + 1, 4096);
    final HuffmanCodeBook huffmanCodeBook = new HuffmanCodeBook(dynamicByteBuffer);
    if (huffmanCodeBook.size() == 1) {
      return decodeConstant(huffmanCodeBook);
    }
    return decodeWith(huffmanCodeBook, dynamicByteBuffer);
  }

  private int[][] decodeConstant(final HuffmanCodeBook huffmanCodeBook) {
    Preconditions.checkArgument(huffmanCodeBook.size() == 1);
    final var paletteIndices = new int[ImageTile.HEIGHT][ImageTile.WIDTH];
    final int paletteIndex = huffmanCodeBook.getPaletteIndex(0);
    for (int y = 0; y < ImageTile.HEIGHT; ++y) {
      for (int x = 0; x < ImageTile.WIDTH; ++x) {
        paletteIndices[y][x] = paletteIndex;
      }
    }
    return paletteIndices;
  }

  private int[][] decodeWith(final HuffmanCodeBook huffmanCodeBook, final DynamicByteBuffer dynamicByteBuffer) {
    Preconditions.checkArgument(huffmanCodeBook.size() > 1);
    final var paletteIndices = new int[ImageTile.HEIGHT][ImageTile.WIDTH];
    int currentByte = dynamicByteBuffer.nextByte();
    int bitCount = 8;
    int pixelIndex = 0;
    while (pixelIndex < ImageTile.PIXEL_COUNT) {
      if (huffmanCodeBook.isColor()) {
        final int y = pixelIndex / ImageTile.WIDTH;
        final int x = pixelIndex % ImageTile.WIDTH;
        paletteIndices[y][x] = huffmanCodeBook.getPaletteIndex();
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
    return paletteIndices;
  }
}
