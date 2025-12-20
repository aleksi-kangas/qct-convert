package com.github.aleksikangas.qct.ui.events.export;

public record ExportFailureEvent(Throwable throwable) {
  public interface Aware {
    void onExportFailure(ExportFailureEvent event);
  }
}
