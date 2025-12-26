package com.github.aleksikangas.qct.core.color;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.awt.*;
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

  public Palette {
    Preconditions.checkArgument(colors.length == SIZE);
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

  /**
   * Get the {@link Color} of the given palette index.
   *
   * @param paletteIndex to obtain
   * @return {@link Color} of the given palette index
   */
  public Color getColor(final int paletteIndex) {
    Objects.checkIndex(paletteIndex, SIZE);
    return colors[paletteIndex];
  }

  /**
   * Get the 8-bit RGBA color value, packed as integer (bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue)
   *
   * @param paletteIndex to obtain
   * @return 8-bit RGBA color value
   */
  public int getRGBA(final int paletteIndex) {
    Objects.checkIndex(paletteIndex, SIZE);
    return colors[paletteIndex].getRGB();
  }

  public int[] asRGBA() {
    final var rgba = new int[SIZE];
    Arrays.parallelSetAll(rgba, i -> colors[i].getRGB());
    return rgba;
  }

  public byte[] redBytes() {
    final var redBytes = new byte[SIZE];
    for (int paletteIndex = 0; paletteIndex < SIZE; ++paletteIndex) {
      redBytes[paletteIndex] = (byte) colors[paletteIndex].getRed();
    }
    return redBytes;
  }

  public byte[] greenBytes() {
    final var greenBytes = new byte[SIZE];
    for (int paletteIndex = 0; paletteIndex < SIZE; ++paletteIndex) {
      greenBytes[paletteIndex] = (byte) colors[paletteIndex].getGreen();
    }
    return greenBytes;
  }

  public byte[] blueBytes() {
    final var blueBytes = new byte[SIZE];
    for (int paletteIndex = 0; paletteIndex < SIZE; ++paletteIndex) {
      blueBytes[paletteIndex] = (byte) colors[paletteIndex].getBlue();
    }
    return blueBytes;
  }
}
