package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.reader.QctReader;

import java.nio.channels.AsynchronousFileChannel;

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
                             String qc3Url) implements Parseable {
  @Override
  public String toString() {
    return String.format("\t\t\tSize: %d\n", size) + String.format("\t\t\tQC3 URL: %s", qc3Url);
  }

  public static final class Parser extends AbstractParser<DigitalMapShop> {

    @Override
    public Class<DigitalMapShop> parseableClass() {
      return DigitalMapShop.class;
    }

    @Override
    public DigitalMapShop parse(final AsynchronousFileChannel asyncFileChannel,
                                final long byteOffset,
                                final ParserRegistry parserRegistry) {
      return new DigitalMapShop(QctReader.readInt(asyncFileChannel, byteOffset),
                                QctReader.readStringFromPointer(asyncFileChannel, byteOffset + 0x04L));
    }
  }
}
