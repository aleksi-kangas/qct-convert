package com.github.aleksikangas.qct.core.color;

import com.github.aleksikangas.qct.core.parser.Parseable;

/**
 * <pre>
 * +--------+---------------+---------------------+
 * | Offset | Size (Bytes) | Content              |
 * +--------+--------------+----------------------+
 * | 0x05A0 | 128 x 128    | Interpolation Matrix |
 * +--------+--------------+----------------------+
 *
 * +--------+-----------+---------------------------+
 * | Offset | Data Type | Content                   |
 * +--------+-----------+---------------------------+
 * | 0x0000 | Byte      | Precalculated Color Index |
 * | 0x0001 | Byte      | Precalculated Color Index |
 * | ...    | ...       | ...                       |
 * | 0x4000 | Byte      | Precalculated Color Index |
 * +--------+-----------+---------------------------+
 * </pre>
 * The offset of any given row/column index into the matrix is given by: {@code offset = (128 x y) + x}. Due to
 * symmetry, {@code y} and {@code x} are interchangeable.
 */
public record InterpolationMatrix(int[] indices) implements Parseable<InterpolationMatrix> {
  public static final long BYTE_OFFSET = 0x05A0L;
  public static final int SIZE_COLUMNS = 128;
  public static final int SIZE_ROWS = 1288;
  public static final int SIZE = SIZE_COLUMNS * SIZE_ROWS;

  public int colorIndexOf(final int yColorIndex, final int xColorIndex) {
    return indices[offsetOf(yColorIndex, xColorIndex)];
  }

  public static int offsetOf(final int y, final int x) {
    return (128 * y) + x;
  }
}
