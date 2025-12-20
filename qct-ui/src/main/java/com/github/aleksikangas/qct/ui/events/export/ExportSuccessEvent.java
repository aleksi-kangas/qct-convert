package com.github.aleksikangas.qct.ui.events.export;

public record ExportSuccessEvent() {
  public interface Aware {
    void onExportSuccess(ExportSuccessEvent event);
  }
}
