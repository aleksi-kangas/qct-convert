package com.github.aleksikangas.qct.core.parser.task;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.ParseableAware;

import javax.annotation.Nonnull;

public interface ParseTask<P extends Parseable<P>> extends ParseableAware<P> {
  @Nonnull
  P parse();
}
