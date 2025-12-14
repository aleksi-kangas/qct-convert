package com.github.aleksikangas.qct.core.color;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link InterpolationMatrix}.
 */
final class InterpolationMatrixTest {
  @Test
  void test() {
    final var interpolationMatrix = generateSymmetric();
    assertEquals(2, interpolationMatrix.colorIndexOf(2, 0));
    assertEquals(2, interpolationMatrix.colorIndexOf(0, 2));
    assertEquals(258, interpolationMatrix.colorIndexOf(2, 2));
  }

  private static InterpolationMatrix generateSymmetric() {
    final int[] m = new int[InterpolationMatrix.SIZE];
    IntStream.range(0, InterpolationMatrix.SIZE_ROWS)
        .forEach(y -> IntStream.range(0, InterpolationMatrix.SIZE_COLUMNS)
            .forEach(x -> {
              final int minIdx = Math.min(y, x);
              final int maxIdx = Math.max(y, x);
              final int value = (minIdx * InterpolationMatrix.SIZE_COLUMNS) + maxIdx;
              m[(y * InterpolationMatrix.SIZE_COLUMNS) + x] = value;
            }));
    return new InterpolationMatrix(m);
  }
}
