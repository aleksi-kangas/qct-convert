package com.github.aleksikangas.qct.core.color.parser;

import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.parser.AbstractParser;
import com.github.aleksikangas.qct.core.parser.feature.AsyncFileChannelAware;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import com.github.aleksikangas.qct.core.reader.QctReader;
import jakarta.enterprise.context.ApplicationScoped;

import javax.annotation.Nonnull;
import java.nio.channels.AsynchronousFileChannel;

/**
 * A {@link com.github.aleksikangas.qct.core.parser.Parser} for {@link InterpolationMatrix}.
 */
@ApplicationScoped
public class InterpolationMatrixParser extends AbstractParser<InterpolationMatrix, InterpolationMatrixParser.Task> {
  @Nonnull
  @Override
  public Class<InterpolationMatrix> parseableClass() {
    return InterpolationMatrix.class;
  }

  @Nonnull
  @Override
  public InterpolationMatrix parse(final Task parseTask) {
    return new InterpolationMatrix(QctReader.readBytes(parseTask.asyncFileChannel,
                                                       InterpolationMatrix.BYTE_OFFSET,
                                                       InterpolationMatrix.SIZE));
  }

  public record Task(AsynchronousFileChannel asyncFileChannel) implements ParseTask, AsyncFileChannelAware {
  }
}
