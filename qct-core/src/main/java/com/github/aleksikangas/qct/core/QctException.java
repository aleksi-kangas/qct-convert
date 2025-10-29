package com.github.aleksikangas.qct.core;

/**
 * A base for all checked QCT {@link Exception}s.
 */
public class QctException extends Exception {
  public QctException(final String message) {
    super(message);
  }

  public QctException(final Throwable cause) {
    super(cause);
  }

  public QctException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
