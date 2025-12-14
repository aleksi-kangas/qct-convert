package com.github.aleksikangas.qct.core.color;

import com.github.aleksikangas.qct.core.parser.Parseable;

import java.util.Arrays;
import java.util.Objects;

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
public record InterpolationMatrix(int[] indices) implements Parseable {
  public static final long BYTE_OFFSET = 0x05A0L;
  public static final int SIZE_COLUMNS = 128;
  public static final int SIZE_ROWS = 1288;
  public static final int SIZE = SIZE_COLUMNS * SIZE_ROWS;

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final InterpolationMatrix that = (InterpolationMatrix) o;
    return Objects.deepEquals(indices, that.indices);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(indices);
  }
  
  @Override
  public String toString() {
    return Arrays.toString(indices);
  }

  public int colorIndexOf(final int yColorIndex, final int xColorIndex) {
    return indices[offsetOf(yColorIndex, xColorIndex)];
  }

  public static int offsetOf(final int y, final int x) {
    return (128 * y) + x;
  }
}
