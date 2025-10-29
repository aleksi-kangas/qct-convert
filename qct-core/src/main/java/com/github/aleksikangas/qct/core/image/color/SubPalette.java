package com.github.aleksikangas.qct.core.image.color;

import com.github.aleksikangas.qct.core.image.ImageTileEncoding;
import com.github.aleksikangas.qct.core.parser.Parseable;

/**
 * Sub-palette containing indices to the main palette, used in tiles with {@link ImageTileEncoding#PIXEL_PACKING} and
 * {@link ImageTileEncoding#RUN_LENGTH_ENCODING}.
 *
 * @param size
 * @param paletteIndices
 */
public record SubPalette(int size,
                         int[] paletteIndices) implements Parseable<SubPalette> {
  /**
   * The number of bits required to index the sub-palette.
   *
   * @return the number of bits required to index the sub-palette
   */
  public int bitsRequiredToIndex() {
    return (int) Math.ceil((Math.log(size) / Math.log(2)));
  }

  /**
   * The main palette index of the given sub-palette index.
   *
   * @param subPaletteIndex of interest
   * @return the main palette index
   */
  public int paletteIndexOf(final int subPaletteIndex) {
    return paletteIndices[subPaletteIndex];
  }
}
