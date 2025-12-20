package com.github.aleksikangas.qct.core;

import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.georef.GeoreferencingCoefficients;
import com.github.aleksikangas.qct.core.image.ImageIndex;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.Parsers;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.Executors;

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
public record QctFile(Path path,
                      Metadata metadata,
                      GeoreferencingCoefficients georeferencingCoefficients,
                      Palette palette,
                      InterpolationMatrix interpolationMatrix,
                      ImageIndex imageIndex) implements Parseable {
  @Nonnull
  @Override
  public String toString() {
    return "Metadata:" + "\n" + metadata.toString() + "\n" + "Georeferencing Coefficients:" + "\n" + georeferencingCoefficients.toString();
  }

  public static QctFile parse(final Path path) {
    try (final AsynchronousFileChannel asyncFileChannel = AsynchronousFileChannel.open(path,
                                                                                       Set.of(StandardOpenOption.READ),
                                                                                       Executors.newVirtualThreadPerTaskExecutor())) {
      return Parsers.execute(QctFileParser.class, new QctFileParser.Task(asyncFileChannel, path));
    } catch (final IOException e) {
      throw new QctRuntimeException(e);
    }
  }
}
