package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.reader.QctReader;

import java.nio.channels.AsynchronousFileChannel;

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
                         double east) implements Parseable {
  @Override
  public String toString() {
    return String.format("{North: %.6f, East: %.6f}", north, east);
  }

  public static final class Parser extends AbstractParser<DatumShift> {
    @Override
    public Class<DatumShift> parseableClass() {
      return DatumShift.class;
    }

    @Override
    public DatumShift parse(final AsynchronousFileChannel asyncFileChannel,
                            final long byteOffset,
                            final ParserRegistry parserRegistry) {
      return new DatumShift(QctReader.readDouble(asyncFileChannel, byteOffset),
                            QctReader.readDouble(asyncFileChannel, byteOffset + 0x08L));
    }
  }
}
