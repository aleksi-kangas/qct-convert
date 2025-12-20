package com.github.aleksikangas.qct.ui.export;

import java.util.Objects;

public enum ExportFormat {
  PNG(".png");

  private final String extension;

  ExportFormat(final String extension) {
    this.extension = Objects.requireNonNull(extension);
  }

  public String extension() {
    return extension;
  }
}
