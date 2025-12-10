package com.github.aleksikangas.qct.core.parser.registry;

import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.CDI;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public final class ParserRegistryImpl implements ParserRegistry {
  private final Map<Class<? extends Parseable>, Parser<? extends Parseable, ? extends ParseTask<?>>> parsers = new HashMap<>();

  @Override
  public <P extends Parseable, T extends ParseTask<P>> void register(final Parser<P, T> parser) {
    parsers.put(parser.parseableClass(), parser);
  }

  @Override
  public <P extends Parseable, T extends ParseTask<P>> Parser<P, T> getParser(final Class<Parser<P, T>> parserClass) {
    return CDI.current().select(parserClass).get();
  }

  @Override
  public <P extends Parseable, T extends ParseTask<P>> P parse(final Class<Parser<P, T>> parserClass, final T task) {
    return getParser(parserClass).parse(task);
  }

  @Override
  public <P extends Parseable, T extends ParseTask<P>> CompletableFuture<P> parseAsync(final Class<Parser<P, T>> parserClass, final T task) {
    return getParser(parserClass).parseAsync(task);
  }
}
