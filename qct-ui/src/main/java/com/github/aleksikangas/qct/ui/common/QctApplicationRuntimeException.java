package com.github.aleksikangas.qct.ui.common;

public final class QctApplicationRuntimeException extends RuntimeException {
  public QctApplicationRuntimeException(final String message) {
    super(message);
  }

  public QctApplicationRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public QctApplicationRuntimeException(final Throwable cause) {
    super(cause);
  }
}
