package com.github.aleksikangas.qct.core.image.color;

import com.github.aleksikangas.qct.core.image.ImageTileEncoding;

/**
 * Defines the sub-palette size.
 */
public enum SubPaletteSizeType {
  /**
   * X.
   *
   * @see ImageTileEncoding#RUN_LENGTH_ENCODING
   */
  NORMAL,
  /**
   * 256 - X.
   *
   * @see ImageTileEncoding#PIXEL_PACKING
   */
  INVERSE
}
