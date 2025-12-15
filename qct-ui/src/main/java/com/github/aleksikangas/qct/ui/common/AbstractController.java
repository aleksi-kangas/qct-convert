package com.github.aleksikangas.qct.ui.common;

import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import jakarta.enterprise.event.ObservesAsync;

import javax.swing.SwingUtilities;

public abstract class AbstractController<T extends AbstractPanel> implements DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware {
  protected T panel;

  @Override
  public void onDecodeSuccess(@ObservesAsync final DecodeSuccessEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeSuccess(event));
  }

  @Override
  public void onDecodeFailure(@ObservesAsync final DecodeFailureEvent event) {
    SwingUtilities.invokeLater(() -> panel.onDecodeFailure(event));
  }

  public T getPanel() {
    return panel;
  }
}
