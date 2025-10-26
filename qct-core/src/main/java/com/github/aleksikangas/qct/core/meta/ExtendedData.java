package com.github.aleksikangas.qct.core.meta;

import com.github.aleksikangas.qct.core.parser.Parseable;

import javax.annotation.Nonnull;

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
                           DigitalMapShop digitalMapShop) implements Parseable<ExtendedData> {
  @Nonnull
  @Override
  public String toString() {
    return String.format("\t\tMap Type: %s\n", mapType) +
           String.format("\t\tDatum Shift: %s\n", datumShift) +
           String.format("\t\tDisk Name: %s\n", diskName) +
           String.format("\t\tLicence Information: \n%s\n", licenseInformation) +
           String.format("\t\tAssociated Data: %s\n", associatedData) +
           String.format("\t\tDigital Map Shop: \n%s", digitalMapShop);
  }
}
