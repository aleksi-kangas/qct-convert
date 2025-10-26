package com.github.aleksikangas.qct.core.color.parsers;

import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.AsyncReadable;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link InterpolationMatrix}.
 */
public final class InterpolationMatrixParser implements Parser<InterpolationMatrix, InterpolationMatrixParser.Task> {

  @Nonnull
  @Override
  public Class<InterpolationMatrix> parseableClass() {
    return InterpolationMatrix.class;
  }

  @Nonnull
  @Override
  public InterpolationMatrix execute(final Task parseTask) {
    return parseTask.parse();
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
