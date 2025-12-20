package com.github.aleksikangas.qct.ui.events.export;

import com.github.aleksikangas.qct.ui.export.ExportFormat;

import java.nio.file.Path;

public record ExportRequestEvent(ExportFormat exportFormat,
                                 Path exportPath) {
  public interface Aware {
    void onExportRequest(ExportRequestEvent event);
  }
}
