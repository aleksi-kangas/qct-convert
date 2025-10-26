package com.github.aleksikangas.qct.core.parser;

import java.nio.channels.AsynchronousFileChannel;

/**
 * A parser for {@link Parseable} data.
 *
 * @param <T> parseable data
 */
public interface Parser<T extends Parseable> {
  /**
   * @return the {@link Parseable} data class
   */
  Class<T> parseableClass();

  /**
   * Parses {@link Parseable} data.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset within the file
   * @param parserRegistry   registry of {@link Parser}s
   * @return {@link Parseable} data
   */
  T parse(AsynchronousFileChannel asyncFileChannel, long byteOffset, ParserRegistry parserRegistry);
}
