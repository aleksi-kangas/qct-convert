package com.github.aleksikangas.qct.core.parser;

public abstract class AbstractParser<T extends Parseable> implements Parser<T> {
  @Override
  public abstract Class<T> parseableClass();
}
