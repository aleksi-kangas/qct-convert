package com.github.aleksikangas.qct.core.parser;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.color.InterpolationMatrix;
import com.github.aleksikangas.qct.core.color.Palette;
import com.github.aleksikangas.qct.core.georef.GeoreferencingCoefficients;
import com.github.aleksikangas.qct.core.meta.*;

import java.util.HashMap;
import java.util.Map;

public final class ParserRegistryImpl implements ParserRegistry {
  private final Map<Class<? extends Parseable>, Parser<? extends Parseable>> parsers = new HashMap<>();

  public ParserRegistryImpl() {
    register(new QctFile.Parser());

    // color
    register(new InterpolationMatrix.Parser());
    register(new Palette.Parser());

    // georef
    register(new GeoreferencingCoefficients.Parser());

    // meta
    register(new DatumShift.Parser());
    register(new DigitalMapShop.Parser());
    register(new ExtendedData.Parser());
    register(new Metadata.Parser());
    register(new LicenseInformation.Parser());
    register(new MapOutline.Parser());
    register(new MapOutline.Point.Parser());
    register(new SerialNumber.Parser());
  }

  @Override
  public <T extends Parseable> Parser<T> getParser(final Class<T> parseableClass) {
    if (parsers.containsKey(parseableClass)) {
      //noinspection unchecked
      return (Parser<T>) parsers.get(parseableClass);
    }
    throw new IllegalStateException(String.format("Parser for %s not registered", parseableClass.getName()));
  }

  private void register(final Parser<? extends Parseable> parser) {
    parsers.put(parser.parseableClass(), parser);
  }
}
