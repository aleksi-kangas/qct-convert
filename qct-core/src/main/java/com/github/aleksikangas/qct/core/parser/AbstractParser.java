package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractParser<P extends Parseable, T extends ParseTask<P>> implements Parser<P, T> {
  protected final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  @Nonnull
  @Override
  public CompletableFuture<P> parseAsync(final T parseTask) {
    return CompletableFuture.supplyAsync(() -> parse(parseTask), executorService);
  }
}
