package com.github.aleksikangas.qct.core;

import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.georef.GeoreferencingCoefficients;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.ParserRegistryImpl;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * <pre>
 * +--------+---------------+---------------------------------------------------+
 * | Offset | Size (Bytes) | Content                                            |
 * +--------+--------------+----------------------------------------------------+
 * | 0x0000 | 24 x 4       | Metadata - 24 Integers/Pointers                    |
 * | 0x0060 | 40 x 8       | Geographical Referencing Coefficients - 40 Doubles |
 * | 0x01A0 | 256 x 4      | Palette - 128 of 256 Colors                        |
 * | 0x05A0 | 128 x 128    | Interpolation Matrix                               |
 * | 0x45A0 | w x h x 4    | Image Index Pointers - QC3 Files Omit This         |
 * | -      | -            | File Body - Text Strings and Compressed Image Data |
 * +--------+--------------+----------------------------------------------------+
 * </pre>
 */
public record QctFile(Metadata metadata,
                      GeoreferencingCoefficients georeferencingCoefficients,
                      Palette palette,
                      InterpolationMatrix interpolationMatrix) implements Parseable {
  @Override
  public String toString() {
    return "Metadata:" +
           "\n" +
           metadata.toString() +
           "\n" +
           "Georeferencing Coefficients:" +
           "\n" +
           georeferencingCoefficients.toString();
  }

  static void main(final String[] args) throws IOException {
    final Path path = Path.of(args[0]);
    final ParserRegistry parserRegistry = new ParserRegistryImpl();
    try (final AsynchronousFileChannel asyncFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ)) {
      final QctFile qctFile = parserRegistry.getParser(QctFile.class)
                                            .parse(asyncFileChannel, 0x00L, parserRegistry);
      System.out.println(qctFile);
    }
  }

  public static final class Parser extends AbstractParser<QctFile> {
    @Override
    public Class<QctFile> parseableClass() {
      return QctFile.class;
    }

    @Override
    public QctFile parse(final AsynchronousFileChannel asyncFileChannel,
                         final long byteOffset,
                         final ParserRegistry parserRegistry) {
      return new QctFile(parserRegistry.getParser(Metadata.class)
                                       .parse(asyncFileChannel, byteOffset + Metadata.BYTE_OFFSET, parserRegistry),
                         parserRegistry.getParser(GeoreferencingCoefficients.class)
                                       .parse(asyncFileChannel,
                                              byteOffset + GeoreferencingCoefficients.BYTE_OFFSET,
                                              parserRegistry),
                         parserRegistry.getParser(Palette.class)
                                       .parse(asyncFileChannel, byteOffset + Palette.BYTE_OFFSET, parserRegistry),
                         parserRegistry.getParser(InterpolationMatrix.class)
                                       .parse(asyncFileChannel,
                                              byteOffset + InterpolationMatrix.BYTE_OFFSET,
                                              parserRegistry));
    }
  }
}
