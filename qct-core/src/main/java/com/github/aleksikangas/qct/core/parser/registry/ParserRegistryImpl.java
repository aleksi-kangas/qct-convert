package com.github.aleksikangas.qct.core.parser.registry;

import com.github.aleksikangas.qct.core.QctFileParser;
import com.github.aleksikangas.qct.core.color.parser.InterpolationMatrixParser;
import com.github.aleksikangas.qct.core.color.parser.PaletteParser;
import com.github.aleksikangas.qct.core.georef.parser.GeoreferencingCoefficientsParser;
import com.github.aleksikangas.qct.core.image.parser.ImageIndexParser;
import com.github.aleksikangas.qct.core.image.parser.ImageTileParser;
import com.github.aleksikangas.qct.core.image.parser.SubPaletteParser;
import com.github.aleksikangas.qct.core.meta.parser.*;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public final class ParserRegistryImpl implements ParserRegistry {
  private final Map<Class<? extends Parseable<?>>, Parser<? extends Parseable<?>, ? extends ParseTask<?>>> parsers = new HashMap<>();

  public ParserRegistryImpl(final ExecutorService executorService) {
    register(new QctFileParser(executorService));

    // color
    register(new InterpolationMatrixParser(executorService));
    register(new PaletteParser(executorService));

    // georef
    register(new GeoreferencingCoefficientsParser(executorService));

    // image
    register(new ImageIndexParser(executorService));
    register(new ImageTileParser(executorService));
    register(new SubPaletteParser(executorService));

    // meta
    register(new DatumShiftParser(executorService));
    register(new DigitalMapShopParser(executorService));
    register(new ExtendedDataParser(executorService));
    register(new MetadataParser(executorService));
    register(new LicenseInformationParser(executorService));
    register(new MapOutlineParser(executorService));
    register(new SerialNumberParser(executorService));
  }

  @Override
  public <P extends Parseable<P>, T extends ParseTask<P>> Parser<P, T> getParser(final Class<P> parseableClass) {
    if (parsers.containsKey(parseableClass)) {
      //noinspection unchecked
      return (Parser<P, T>) parsers.get(parseableClass);
    }
    throw new IllegalStateException(String.format("Parser for %s not registered", parseableClass.getName()));
  }

  @Override
  public <T extends Parseable<T>> T parse(final ParseTask<T> task) {
    return getParser(task.parseableClass()).execute(task);
  }

  @Override
  public <T extends Parseable<T>> CompletableFuture<T> parseAsync(final ParseTask<T> task) {
    return getParser(task.parseableClass()).executeAsync(task);
  }

  private <P extends Parseable<P>, T extends ParseTask<P>> void register(final Parser<P, T> parser) {
    parsers.put(parser.parseableClass(), parser);
  }
}
