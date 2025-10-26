package com.github.aleksikangas.qct.core.parser.registry;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;

/**
 * A registry for {@link Parser}s.
 */
public interface ParserRegistry {
  <P extends Parseable<P>, T extends ParseTask<P>> Parser<P, T> getParser(Class<P> parseableClass);

  <T extends Parseable<T>> T parse(ParseTask<T> task);
}
