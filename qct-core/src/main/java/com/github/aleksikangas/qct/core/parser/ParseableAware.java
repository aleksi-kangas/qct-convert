package com.github.aleksikangas.qct.core.parser;


import javax.annotation.Nonnull;

/**
 * An interface for {@link Parseable}-class aware implementations.
 *
 * @param <P> parseable data type
 */
public interface ParseableAware<P extends Parseable> {
  @Nonnull
  Class<P> parseableClass();
}
