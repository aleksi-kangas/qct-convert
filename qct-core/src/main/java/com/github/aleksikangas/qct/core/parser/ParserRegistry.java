package com.github.aleksikangas.qct.core.parser;

/**
 * A registry for {@link Parser}s.
 */
public interface ParserRegistry {

  <T extends Parseable> Parser<T> getParser(Class<T> parseableClass);
}
