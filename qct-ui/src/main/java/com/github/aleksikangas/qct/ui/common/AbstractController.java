package com.github.aleksikangas.qct.ui.common;

import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;
import jakarta.enterprise.event.ObservesAsync;

import javax.swing.SwingUtilities;

public abstract class AbstractController<T extends AbstractPanel> implements DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware, ExportSuccessEvent.Aware, ExportFailureEvent.Aware {
  protected T panel;

  @Override
  public void onDecodeSuccess(@ObservesAsync final DecodeSuccessEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeSuccess(event));
  }

  @Override
  public void onDecodeFailure(@ObservesAsync final DecodeFailureEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeFailure(event));
  }

  @Override
  public void onExportSuccess(@ObservesAsync final ExportSuccessEvent event) {
    SwingUtilities.invokeLater(() -> panel.onExportSuccess(event));
  }

  @Override
  public void onExportFailure(@ObservesAsync final ExportFailureEvent event) {
    SwingUtilities.invokeLater(() -> panel.onExportFailure(event));
  }

  public T getPanel() {
    return panel;
  }
}
