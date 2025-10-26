package com.github.aleksikangas.qct.core.meta;

import java.util.Arrays;

/**
 * <pre>
 * +--------+-----------+--------------------------------------------------------+
 * | Offset | Data Type | Content                                                |
 * +--------+-----------+--------------------------------------------------------+
 * | 0x04   | Integer   | File Format Version                                    |
 * |        |           | 0x00000002 – Quick Chart                               |
 * |        |           | 0x00000004 – Quick Chart supporting License Management |
 * |        |           | 0x20000001 – QC3 Format                                |
 * +--------+-----------+--------------------------------------------------------+
 * </pre>
 */
public enum FileFormatVersion {
  QUICK_CHART(0x00000002),
  QUICK_CHART_SUPPORTING_LICENSE_MANAGEMENT(0x00000004),
  QC3(0x20000001);

  private final int value;

  FileFormatVersion(final int value) {
    this.value = value;
  }

  public static FileFormatVersion of(final int value) {
    return Arrays.stream(FileFormatVersion.values())
                 .filter(f -> f.value == value)
                 .findFirst()
                 .orElseThrow();
  }

  @Override
  public String toString() {
    return switch (this) {
      case QUICK_CHART -> "Quick Chart";
      case QUICK_CHART_SUPPORTING_LICENSE_MANAGEMENT -> "Quick Chart supporting License Management";
      case QC3 -> "QC3 Format";
    };
  }
}
