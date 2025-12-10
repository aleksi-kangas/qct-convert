package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import jakarta.enterprise.inject.spi.CDI;

import java.util.concurrent.CompletableFuture;

public final class Parsers {
  public static <P extends Parseable, T extends ParseTask<P>> P execute(final Class<? extends Parser<P, T>> parserClass, final T parseTask) {
    return CDI.current().select(parserClass).get().parse(parseTask);
  }

  public static <P extends Parseable, T extends ParseTask<P>> CompletableFuture<P> executeAsync(final Class<? extends Parser<P, T>> parserClass, final T parseTask) {
    return CDI.current().select(parserClass).get().parseAsync(parseTask);
  }
}
