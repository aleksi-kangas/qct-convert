package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.QctRuntimeException;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public abstract class AbstractParser<P extends Parseable<P>, T extends ParseTask<P>> implements Parser<P, T> {
  protected final ExecutorService executorService;

  protected AbstractParser(final ExecutorService executorService) {
    this.executorService = Objects.requireNonNull(executorService);
  }

  @Nonnull
  @Override
  public final P execute(T parseTask) {
    try {
      return CompletableFuture.supplyAsync(parseTask::parse, executorService).get();
    } catch (final ExecutionException e) {
      throw new QctRuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctRuntimeException(e);
    }
  }

  @Nonnull
  @Override
  public final CompletableFuture<P> executeAsync(T parseTask) {
    return CompletableFuture.supplyAsync(parseTask::parse, executorService);
  }
}
