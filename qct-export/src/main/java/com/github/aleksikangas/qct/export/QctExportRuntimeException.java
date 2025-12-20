package com.github.aleksikangas.qct.export;

public final class QctExportRuntimeException extends RuntimeException {
  public QctExportRuntimeException(String message) {
    super(message);
  }

  public QctExportRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public QctExportRuntimeException(final Throwable cause) {
    super(cause);
  }
}
