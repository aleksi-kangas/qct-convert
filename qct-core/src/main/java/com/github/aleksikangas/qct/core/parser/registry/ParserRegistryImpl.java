package com.github.aleksikangas.qct.core.parser.registry;

import com.github.aleksikangas.qct.core.QctFileParser;
import com.github.aleksikangas.qct.core.color.parsers.InterpolationMatrixParser;
import com.github.aleksikangas.qct.core.color.parsers.PaletteParser;
import com.github.aleksikangas.qct.core.georef.parsers.GeoreferencingCoefficientsParser;
import com.github.aleksikangas.qct.core.meta.parsers.*;
import com.github.aleksikangas.qct.core.parser.Parseable;
import com.github.aleksikangas.qct.core.parser.Parser;
import com.github.aleksikangas.qct.core.parser.task.ParseTask;

import java.util.HashMap;
import java.util.Map;

public final class ParserRegistryImpl implements ParserRegistry {
  private final Map<Class<? extends Parseable<?>>, Parser<? extends Parseable<?>, ? extends ParseTask<?>>> parsers = new HashMap<>();

  public ParserRegistryImpl() {
    register(new QctFileParser());

    // color
    register(new InterpolationMatrixParser());
    register(new PaletteParser());

    // georef
    register(new GeoreferencingCoefficientsParser());

    // meta
    register(new DatumShiftParser());
    register(new DigitalMapShopParser());
    register(new ExtendedDataParser());
    register(new MetadataParser());
    register(new LicenseInformationParser());
    register(new MapOutlineParser());
    register(new SerialNumberParser());
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

  private <P extends Parseable<P>, T extends ParseTask<P>> void register(final Parser<P, T> parser) {
    parsers.put(parser.parseableClass(), parser);
  }
}
