package com.github.aleksikangas.qct.ui.common;

import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import jakarta.enterprise.event.ObservesAsync;

public abstract class AbstractController<T extends AbstractPanel> implements DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware {
  protected T panel;

  @Override
  public void onDecodeSuccess(@ObservesAsync final DecodeSuccessEvent event) {
    panel.onDecodeSuccess(event);
  }

  @Override
  public void onDecodeFailure(@ObservesAsync final DecodeFailureEvent event) {
    panel.onDecodeFailure(event);
  }

  public T getPanel() {
    return panel;
  }
}
