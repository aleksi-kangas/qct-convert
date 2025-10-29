package com.github.aleksikangas.qct.core;

/**
 * A base for all QCT {@link RuntimeException}s.
 */
public class QctRuntimeException extends RuntimeException {
  public QctRuntimeException(final String message) {
    super(message);
  }

  public QctRuntimeException(final Throwable cause) {
    super(cause);
  }

  public QctRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
