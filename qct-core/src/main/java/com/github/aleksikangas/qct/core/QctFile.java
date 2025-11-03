package com.github.aleksikangas.qct.core;

import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.georef.GeoreferencingCoefficients;
import com.github.aleksikangas.qct.core.image.ImageIndex;
import com.github.aleksikangas.qct.core.meta.Metadata;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistry;
import com.github.aleksikangas.qct.core.parser.registry.ParserRegistryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.concurrent.ExecutorService;
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
public record QctFile(Metadata metadata,
                      GeoreferencingCoefficients georeferencingCoefficients,
                      Palette palette,
                      InterpolationMatrix interpolationMatrix,
                      ImageIndex imageIndex) implements Parseable<QctFile> {
  private static final Logger LOG = LoggerFactory.getLogger(QctFile.class);

  @Nonnull
  @Override
  public String toString() {
    return "Metadata:" + "\n" + metadata.toString() + "\n" + "Georeferencing Coefficients:" + "\n" + georeferencingCoefficients.toString();
  }

  static void main(final String[] args) throws IOException {
    final Path path = Path.of(args[0]);
    try (final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      final ParserRegistry parserRegistry = new ParserRegistryImpl(executorService);
      try (final AsynchronousFileChannel asyncFileChannel = AsynchronousFileChannel.open(path,
                                                                                         Set.of(StandardOpenOption.READ),
                                                                                         Executors.newVirtualThreadPerTaskExecutor())) {
        final QctFile qctFile = parserRegistry.parse(new QctFileParser.Task(asyncFileChannel, parserRegistry));
        LOG.info("Decode successful");
        System.out.println(qctFile);
      } catch (final QctRuntimeException e) {
        LOG.error("Failed to parse QctFile", e);
      }
    }
  }
}
