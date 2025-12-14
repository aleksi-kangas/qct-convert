package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

/**
 * A parser for {@link Parseable} data.
 *
 * @param <T> parseable data
 */
public interface Parser<P extends Parseable, T extends ParseTask> extends ParseableAware<P> {
  @Nonnull
  P parse(T parseTask);

  @Nonnull
  CompletableFuture<P> parseAsync(T parseTask);
}
