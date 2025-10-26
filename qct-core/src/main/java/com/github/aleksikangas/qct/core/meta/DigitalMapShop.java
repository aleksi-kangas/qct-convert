package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.Parseable;

import javax.annotation.Nonnull;

/**
 * <pre>
 * +--------+-------------------+-----------------------------+
 * | Offset | Data Type         | Content                     |
 * +--------+-------------------+-----------------------------+
 * | 0x00   | Integer           | Structure size, set to 8    |
 * | 0x04   | Pointer to String | Partial URL to QC3 map file |
 * +--------+-------------------+-----------------------------+
 * </pre>
 */
public record DigitalMapShop(int size,
                             String qc3Url) implements Parseable<DigitalMapShop> {
  @Nonnull
  @Override
  public String toString() {
    return String.format("\t\t\tSize: %d\n", size) + String.format("\t\t\tQC3 URL: %s", qc3Url);
  }
}
