package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import jakarta.enterprise.inject.spi.CDI;

import java.util.concurrent.CompletableFuture;

/**
 * Wrappers for accessing {@link Parser} implementations from CDI.
 */
public final class Parsers {
  public static <P extends Parseable, T extends ParseTask> P execute(final Class<? extends Parser<P, T>> parserClass, final T parseTask) {
    return CDI.current().select(parserClass).get().parse(parseTask);
  }

  public static <P extends Parseable, T extends ParseTask> CompletableFuture<P> executeAsync(final Class<? extends Parser<P, T>> parserClass, final T parseTask) {
    return CDI.current().select(parserClass).get().parseAsync(parseTask);
  }

  private Parsers() {
  }
}
