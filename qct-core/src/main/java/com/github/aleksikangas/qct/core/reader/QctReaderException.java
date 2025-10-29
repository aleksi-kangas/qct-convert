package com.github.aleksikangas.qct.core.reader;

import com.github.aleksikangas.qct.core.QctRuntimeException;

/**
 * A {@link QctRuntimeException} indicating runtime error when reading a QCT file.
 */
public final class QctReaderException extends RuntimeException {
  QctReaderException(final String message) {
    super(message);
  }

  QctReaderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  QctReaderException(final Throwable cause) {
    super(cause);
  }
}
