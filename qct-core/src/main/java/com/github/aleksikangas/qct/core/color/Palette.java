package com.github.aleksikangas.qct.core.color;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.util.Arrays;
import java.util.Objects;

/**
 * <pre>
 * +--------+--------------+------------------------------+
 * | Offset | Size (Bytes) | Content                      |
 * +--------+--------------+------------------------------+
 * | 0x01A0 | 256 x 4      | Palette - 128 of 256 Colours |
 * +--------+--------------+------------------------------+
 *
 * +--------+-----------+-------------------------+
 * | Offset | Data Type | Content                 |
 * +--------+-----------+-------------------------+
 * | 0x00   | Byte      | Blue Intensity [0-255]  |
 * | 0x01   | Byte      | Green Intensity [0-255] |
 * | 0x02   | Byte      | Red Intensity [0-255]   |
 * | 0x03   | Byte      | Padding byte, set to 0  |
 * +--------+-----------+-------------------------+
 * </pre>
 */
public record Palette(Color[] colors) implements Parseable {
  public static final long BYTE_OFFSET = 0x01A0L;
  public static final int SIZE = 128;

  public Color getColor(final int paletteIndex) {
    Preconditions.checkArgument(0 <= paletteIndex && paletteIndex < SIZE);
    return colors[paletteIndex];
  }

  @Override
  public boolean equals(final Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final Palette palette = (Palette) o;
    return Objects.deepEquals(colors, palette.colors);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(colors);
  }

  @Nonnull
  @Override
  public String toString() {
    final StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < SIZE; ++i) {
      final Color color = colors[i];
      stringBuilder.append(String.format("\t%3d: [R: %3d, G: %3d, B: %3d]",
                                         i,
                                         color.getRed(),
                                         color.getGreen(),
                                         color.getBlue()));
      if (i < SIZE - 1) {
        stringBuilder.append("\n");
      }
    }
    return stringBuilder.toString();
  }

}
