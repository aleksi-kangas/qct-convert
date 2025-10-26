package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.Parseable;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * <pre>
 * +--------+-----------+---------+
 * | Offset | Data Type | Content |
 * +--------+-----------+---------+
 * | 0x00   | 32 Bytes  | Unknown |
 * +--------+-----------+---------+
 * </pre>
 */
public record SerialNumber(int[] bytes) implements Parseable<SerialNumber> {
  @Nonnull
  @Override
  public String toString() {
    return Arrays.toString(bytes);
  }
}
