package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.Parseable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * <pre>
 * +--------+-------------------+---------------------------------+
 * | Offset | Data Type         | Content                         |
 * +--------+-------------------+---------------------------------+
 * | 0x40   | Integer Bit-Field | Flags                           |
 * |        |                   | Bit 0 - Must have original file |
 * |        |                   | Bit 1 - Allow calibration       |
 * +--------+-------------------+---------------------------------+
 * </pre>
 */
public enum Flag implements Parseable {
  MUST_HAVE_ORIGINAL_FILE(0),
  ALLOW_CALIBRATION(1);

  private final int value;

  Flag(final int value) {
    this.value = value;
  }

  public static Set<Flag> flagsOf(final int x) {
    final List<Flag> flagList = new ArrayList<>();
    if ((x & MUST_HAVE_ORIGINAL_FILE.value) == MUST_HAVE_ORIGINAL_FILE.value) {
      flagList.add(MUST_HAVE_ORIGINAL_FILE);
    }
    if ((x & ALLOW_CALIBRATION.value) == ALLOW_CALIBRATION.value) {
      flagList.add(ALLOW_CALIBRATION);
    }
    return EnumSet.copyOf(flagList);
  }
}
