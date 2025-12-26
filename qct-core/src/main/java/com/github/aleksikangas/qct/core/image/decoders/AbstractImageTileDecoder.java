package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * An abstract base class for all {@link ImageTileDecoder}s.
 */
abstract class AbstractImageTileDecoder implements ImageTileDecoder {
  private static final int[] INTERLACED_ROW_SEQUENCE = new int[]{0, 32, 16, 48, 8, 40, 24, 56, 4, 36, 20, 52, 12, 44, 28, 60, 2, 34, 18, 50, 10, 42, 26, 58, 6, 38, 22, 54, 14, 46, 30, 62, 1, 33, 17, 49, 9, 41, 25, 57, 5, 37, 21, 53, 13, 45, 29, 61, 3, 35, 19, 51, 11, 43, 27, 59, 7, 39, 23, 55, 15, 47, 31, 63};
  private static final int[] DEINTERLACED_ROW_SEQUENCE = deinterlacedRowSequence();

  @Nonnull
  @Override
  public final int[][] decode(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) throws QctDecoderException {
    final int[][] paletteIndices = decodePaletteIndices(asyncFileChannel, byteOffset);
    return deinterlaceRows(paletteIndices);
  }

  /**
   * Decode the pixels of the {@link ImageTile} based on the {@link ImageTileEncoding} used.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       byte offset of the {@link ImageTile}
   * @return decoded pixels of the {@link ImageTile}, as {@link com.github.aleksikangas.qct.core.color.Palette} indices
   * @implSpec Shall not perform any deinterlacing of the rows.
   * @see #deinterlaceRows(int[][])
   */
  @Nonnull
  protected abstract int[][] decodePaletteIndices(AsynchronousFileChannel asyncFileChannel, long byteOffset) throws QctDecoderException;

  /**
   * Deinterlaces rows of an {@link ImageTile}. The pixel content of each tile is scanned from left to right in rows of
   * 64 pixels. The rows however are not in top to bottom order. Instead, they are interlaced using a bit-reverse
   * sequence. The decompressed tile data must be scanned out row at a time using this row sequence. See Chapter 7.1 in
   * the specification.
   *
   * @param paletteIndices of the {@link ImageTile}
   * @return deinterlaced {@link com.github.aleksikangas.qct.core.color.Palette} indices of the {@link ImageTile}
   */
  private int[][] deinterlaceRows(final int[][] paletteIndices) {
    final var deinterlacedPaletteIndices = new int[ImageTile.HEIGHT][ImageTile.WIDTH];
    for (int i = 0; i < ImageTile.HEIGHT; ++i) {
      deinterlacedPaletteIndices[DEINTERLACED_ROW_SEQUENCE[i]] = paletteIndices[i];
    }
    return deinterlacedPaletteIndices;
  }

  private static int[] deinterlacedRowSequence() {
    final int[] deinterlacedRowSequence = new int[ImageTile.HEIGHT];
    for (int i = 0; i < ImageTile.HEIGHT; ++i) {
      deinterlacedRowSequence[INTERLACED_ROW_SEQUENCE[i]] = i;
    }
    return deinterlacedRowSequence;
  }
}
