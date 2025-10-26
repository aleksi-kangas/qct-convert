package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.reader.QctReader;

import java.nio.channels.AsynchronousFileChannel;
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
public record SerialNumber(int[] bytes) implements Parseable {
  @Override
  public String toString() {
    return Arrays.toString(bytes);
  }

  public static final class Parser extends AbstractParser<SerialNumber> {
    @Override
    public Class<SerialNumber> parseableClass() {
      return SerialNumber.class;
    }

    @Override
    public SerialNumber parse(final AsynchronousFileChannel asyncFileChannel,
                              final long byteOffset,
                              final ParserRegistry parserRegistry) {
      return new SerialNumber(QctReader.readBytes(asyncFileChannel, byteOffset, 32));
    }
  }
}
