package com.github.aleksikangas.qct.core.georef.parsers;

import com.github.aleksikangas.qct.core.georef.GeoreferencingCoefficients;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link GeoreferencingCoefficients}.
 */
public final class GeoreferencingCoefficientsParser
    implements Parser<GeoreferencingCoefficients, GeoreferencingCoefficientsParser.Task> {
  @Nonnull
  @Override
  public Class<GeoreferencingCoefficients> parseableClass() {
    return GeoreferencingCoefficients.class;
  }

  @Nonnull
  @Override
  public GeoreferencingCoefficients execute(final Task parseTask) {
    return parseTask.parse();
  }

  public record Task(AsynchronousFileChannel asyncFileChannel)
      implements ParseTask<GeoreferencingCoefficients>, AsyncReadable {

    @Nonnull
    @Override
    public Class<GeoreferencingCoefficients> parseableClass() {
      return GeoreferencingCoefficients.class;
    }

    @Nonnull
    @Override
    public GeoreferencingCoefficients parse() {
      final double[] easDoubles = QctReader.readDoubles(asyncFileChannel, GeoreferencingCoefficients.BYTE_OFFSET, 10);
      final double[] norDoubles = QctReader.readDoubles(asyncFileChannel,
                                                        GeoreferencingCoefficients.BYTE_OFFSET + 0x50L,
                                                        10);
      final double[] latDoubles = QctReader.readDoubles(asyncFileChannel,
                                                        GeoreferencingCoefficients.BYTE_OFFSET + 0xA0L,
                                                        10);
      final double[] lonDoubles = QctReader.readDoubles(asyncFileChannel,
                                                        GeoreferencingCoefficients.BYTE_OFFSET + 0xF0L,
                                                        10);
      return new GeoreferencingCoefficients(// eas
                                            easDoubles[0],
                                            easDoubles[1],
                                            easDoubles[2],
                                            easDoubles[3],
                                            easDoubles[4],
                                            easDoubles[5],
                                            easDoubles[6],
                                            easDoubles[7],
                                            easDoubles[8],
                                            easDoubles[9],
                                            // nor
                                            norDoubles[0],
                                            norDoubles[1],
                                            norDoubles[2],
                                            norDoubles[3],
                                            norDoubles[4],
                                            norDoubles[5],
                                            norDoubles[6],
                                            norDoubles[7],
                                            norDoubles[8],
                                            norDoubles[9],
                                            // lat
                                            latDoubles[0],
                                            latDoubles[1],
                                            latDoubles[2],
                                            latDoubles[3],
                                            latDoubles[4],
                                            latDoubles[5],
                                            latDoubles[6],
                                            latDoubles[7],
                                            latDoubles[8],
                                            latDoubles[9],
                                            // lon
                                            lonDoubles[0],
                                            lonDoubles[1],
                                            lonDoubles[2],
                                            lonDoubles[3],
                                            lonDoubles[4],
                                            lonDoubles[5],
                                            lonDoubles[6],
                                            lonDoubles[7],
                                            lonDoubles[8],
                                            lonDoubles[9]);
    }
  }
}
