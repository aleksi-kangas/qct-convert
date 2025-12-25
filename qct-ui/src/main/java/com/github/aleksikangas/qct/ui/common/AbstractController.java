package com.github.aleksikangas.qct.ui.common;

import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;
import jakarta.enterprise.event.Observes;

import javax.swing.*;

public abstract class AbstractController<T extends AbstractPanel> implements DecodeRequestEvent.Aware, DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware, ExportSuccessEvent.Aware, ExportFailureEvent.Aware {
  protected T panel;

  @Override
  public void onDecodeRequest(@Observes final DecodeRequestEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeRequest(event));
  }

  @Override
  public void onDecodeSuccess(@Observes final DecodeSuccessEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeSuccess(event));
  }

  @Override
  public void onDecodeFailure(@Observes final DecodeFailureEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeFailure(event));
  }

  @Override
  public void onExportSuccess(@Observes final ExportSuccessEvent event) {
    SwingUtilities.invokeLater(() -> panel.onExportSuccess(event));
  }

  @Override
  public void onExportFailure(@Observes final ExportFailureEvent event) {
    SwingUtilities.invokeLater(() -> panel.onExportFailure(event));
  }

  public T getPanel() {
    return panel;
  }
}
