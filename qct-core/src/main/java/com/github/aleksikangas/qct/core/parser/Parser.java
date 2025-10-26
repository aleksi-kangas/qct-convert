package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import javax.annotation.Nonnull;

/**
 * A parser for {@link Parseable} data.
 *
 * @param <T> parseable data
 */
public interface Parser<P extends Parseable<P>, T extends ParseTask<P>> extends ParseableAware<P> {
  /**
   * Parses {@link Parseable} data.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset within the file
   * @param parserRegistry   registry of {@link Parser}s
   * @return {@link Parseable} data
   */
  @Nonnull
  P execute(T parseTask);
}
