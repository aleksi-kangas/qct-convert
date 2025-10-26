package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.Parseable;

import javax.annotation.Nonnull;

/**
 * <pre>
 * +--------+-----------+-------------------+
 * | Offset | Data Type | Content           |
 * +--------+-----------+-------------------+
 * | 0x00   | Double    | Datum Shift North |
 * | 0x08   | Double    | Datum Shift East  |
 * +--------+-----------+-------------------+
 * </pre>
 */
public record DatumShift(double north,
                         double east) implements Parseable<DatumShift> {
  @Nonnull
  @Override
  public String toString() {
    return String.format("{North: %.6f, East: %.6f}", north, east);
  }
}
