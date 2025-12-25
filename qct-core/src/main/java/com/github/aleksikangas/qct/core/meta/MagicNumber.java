package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.QctRuntimeException;

import java.util.Arrays;

/**
 * <pre>
 * +--------+-----------+--------------------------------------+
 * | Offset | Data Type | Content                              |
 * +--------+-----------+--------------------------------------+
 * | 0x00   | Integer   | Magic Number                         |
 * |        |           | 0x1423D5FE - Quick Chart Information |
 * |        |           | 0x1423D5FF - Quick Chart Map         |
 * +--------+-----------+--------------------------------------+
 * </pre>
 */
public enum MagicNumber {
  QUICK_CHART_INFORMATION(0x1423D5FE),
  QUICK_CHART_MAP(0x1423D5FF);

  private final int value;

  MagicNumber(final int value) {
    this.value = value;
  }

  public static MagicNumber of(final int value) {
    return Arrays.stream(MagicNumber.values())
        .filter(f -> f.value == value)
        .findFirst()
        .orElseThrow(() -> new QctRuntimeException(String.format("Unknown MagicNumber: %d", value)));
  }

  @Override
  public String toString() {
    return switch (this) {
      case QUICK_CHART_INFORMATION -> "Quick CHart Information";
      case QUICK_CHART_MAP -> "Quick Chart Map";
    };
  }
}
