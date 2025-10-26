package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.reader.QctReader;

import java.nio.channels.AsynchronousFileChannel;

/**
 * <pre>
 * +--------+-------------------+---------------------------------------------------+
 * | Offset | Data Type         | Content                                           |
 * +--------+-------------------+---------------------------------------------------+
 * | 0x00   | Pointer to String | Map Type                                          |
 * | 0x04   | Pointer to Array  | Datum Shift (see section 6.2.3)                   |
 * | 0x08   | Pointer to String | Disk Name                                         |
 * | 0x0C   | Integer           | Reserved, set to 0                                |
 * | 0x10   | Integer           | Reserved, set to 0                                |
 * | 0x14   | Pointer to Struct | License Information (Optional, see section 6.2.4) |
 * | 0x18   | Pointer to String | Associated Data                                   |
 * | 0x1C   | Pointer to Struct | Digital Map Shop (Optional, see section 6.2.5)    |
 * +--------+-------------------+---------------------------------------------------+
 * </pre>
 */
public record ExtendedData(String mapType,
                           DatumShift datumShift,
                           String diskName,
                           LicenseInformation licenseInformation,
                           String associatedData,
                           DigitalMapShop digitalMapShop) implements Parseable {
  @Override
  public String toString() {
    return String.format("\t\tMap Type: %s\n", mapType) +
           String.format("\t\tDatum Shift: %s\n", datumShift) +
           String.format("\t\tDisk Name: %s\n", diskName) +
           String.format("\t\tLicence Information: \n%s\n", licenseInformation) +
           String.format("\t\tAssociated Data: %s\n", associatedData) +
           String.format("\t\tDigital Map Shop: \n%s", digitalMapShop);
  }

  public static final class Parser extends AbstractParser<ExtendedData> {
    @Override
    public Class<ExtendedData> parseableClass() {
      return ExtendedData.class;
    }

    @Override
    public ExtendedData parse(final AsynchronousFileChannel asyncFileChannel,
                              final long byteOffset,
                              final ParserRegistry parserRegistry) {
      return new ExtendedData(QctReader.readStringFromPointer(asyncFileChannel, byteOffset),
                              parserRegistry.getParser(DatumShift.class)
                                            .parse(asyncFileChannel,
                                                   QctReader.readPointer(asyncFileChannel, byteOffset + 0x04L),
                                                   parserRegistry),
                              QctReader.readStringFromPointer(asyncFileChannel, byteOffset + 0x08L),
                              parserRegistry.getParser(LicenseInformation.class)
                                            .parse(asyncFileChannel,
                                                   QctReader.readPointer(asyncFileChannel, byteOffset + 0x14L),
                                                   parserRegistry),
                              QctReader.readStringFromPointer(asyncFileChannel, byteOffset + 0x18L),
                              parserRegistry.getParser(DigitalMapShop.class)
                                            .parse(asyncFileChannel,
                                                   QctReader.readPointer(asyncFileChannel, byteOffset + 0x1CL),
                                                   parserRegistry));


    }
  }
}
