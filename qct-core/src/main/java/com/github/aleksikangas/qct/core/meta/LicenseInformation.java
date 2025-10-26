package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.reader.QctReader;

import java.nio.channels.AsynchronousFileChannel;

/**
 * <pre>
 * +--------+-------------------+--------------------------------------------------------------------------------------+
 * | Offset | Data Type         | Content                                                                              |
 * +--------+-------------------+--------------------------------------------------------------------------------------+
 * | 0x00   | Integer           | Identifier of the license.                                                           |
 * |        |                   | This correlates with name of the license file used that must be paired with the map. |
 * | 0x04   | Integer           | Unknown                                                                              |
 * | 0x08   | Integer           | Unknown                                                                              |
 * | 0x0C   | Pointer to String | License description                                                                  |
 * | 0x10   | Pointer to Struct | Serial Number                                                                        |
 * | 0x14   | Integer           | Unknown                                                                              |
 * | 0x18   | 16 Bytes          | Unknown, set to 0                                                                    |
 * | 0x28   | 64 Bytes          | Unknown                                                                              |
 * +--------+-------------------+--------------------------------------------------------------------------------------+
 * </pre>
 */
public record LicenseInformation(int identifier,
                                 String description,
                                 SerialNumber serialNumber) implements Parseable {
  @Override
  public String toString() {
    return String.format("\t\t\tIdentifier: %d\n", identifier) +
           String.format("\t\t\tDescription: %s\n", description) +
           String.format("\t\t\tSerial Number: %s", serialNumber);
  }

  public static final class Parser extends AbstractParser<LicenseInformation> {

    @Override
    public Class<LicenseInformation> parseableClass() {
      return LicenseInformation.class;
    }

    @Override
    public LicenseInformation parse(final AsynchronousFileChannel asyncFileChannel,
                                    final long byteOffset,
                                    final ParserRegistry parserRegistry) {
      return new LicenseInformation(QctReader.readInt(asyncFileChannel, byteOffset),
                                    QctReader.readString(asyncFileChannel, byteOffset + 0x0CL),
                                    parserRegistry.getParser(SerialNumber.class)
                                                  .parse(asyncFileChannel,
                                                         QctReader.readPointer(asyncFileChannel, byteOffset + 0x10L),
                                                         parserRegistry));
    }
  }
}
