package com.github.aleksikangas.qct.ui.export;

import java.util.Objects;

public enum ExportFormat {
  GEO_TIFF(".tiff"),
  PNG(".png");

  private final String extension;

  ExportFormat(final String extension) {
    this.extension = Objects.requireNonNull(extension);
  }

  public String extension() {
    return extension;
  }

  @Override
  public String toString() {
    return switch (this) {
      case GEO_TIFF -> "GeoTIFF";
      case PNG -> "PNG";
    };
  }
}
