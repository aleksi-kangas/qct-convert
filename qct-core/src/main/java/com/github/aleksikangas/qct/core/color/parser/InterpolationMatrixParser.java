package com.github.aleksikangas.qct.core.color.parser;

import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutorService;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link InterpolationMatrix}.
 */
public final class InterpolationMatrixParser extends AbstractParser<InterpolationMatrix, InterpolationMatrixParser.Task> {
  public InterpolationMatrixParser(final ExecutorService executorService) {
    super(executorService);
  }

  @Nonnull
  @Override
  public Class<InterpolationMatrix> parseableClass() {
    return InterpolationMatrix.class;
  }

  public record Task(AsynchronousFileChannel asyncFileChannel)
      implements ParseTask<InterpolationMatrix>, AsyncReadable {
    @Nonnull
    @Override
    public Class<InterpolationMatrix> parseableClass() {
      return InterpolationMatrix.class;
    }

    @Nonnull
    @Override
    public InterpolationMatrix parse() {
      return new InterpolationMatrix(QctReader.readBytes(asyncFileChannel,
                                                         InterpolationMatrix.BYTE_OFFSET,
                                                         InterpolationMatrix.SIZE));
    }
  }
}
