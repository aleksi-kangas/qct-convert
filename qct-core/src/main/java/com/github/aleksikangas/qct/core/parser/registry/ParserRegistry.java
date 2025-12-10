package com.github.aleksikangas.qct.core.parser.registry;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import java.util.concurrent.CompletableFuture;

/**
 * A registry for {@link Parser}s.
 */
public interface ParserRegistry {
  <P extends Parseable, T extends ParseTask<P>> void register(Parser<P, T> parser);

  <P extends Parseable, T extends ParseTask<P>> Parser<P, T> getParser(Class<Parser<P, T>> parserClass);

  <P extends Parseable, T extends ParseTask<P>> P parse(Class<Parser<P, T>> parserClass, T parseTask);

  <P extends Parseable, T extends ParseTask<P>> CompletableFuture<P> parseAsync(Class<Parser<P, T>> parserClass, T parseTask);
}
