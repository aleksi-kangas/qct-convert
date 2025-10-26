package com.github.aleksikangas.qct.core.reader;

public class QctReaderException extends RuntimeException {
  public QctReaderException(final String message) {
    super(message);
  }

  public QctReaderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public QctReaderException(final Throwable cause) {
    super(cause);
  }
}
