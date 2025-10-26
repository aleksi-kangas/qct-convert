package com.github.aleksikangas.qct.core.parser;


import javax.annotation.Nonnull;

public interface ParseableAware<P extends Parseable<P>> {
  @Nonnull
  Class<P> parseableClass();
}
