package com.github.aleksikangas.qct.ui.common;

import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;

import javax.swing.JPanel;
import java.awt.LayoutManager;

public abstract class AbstractPanel extends JPanel implements DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware {
  protected AbstractPanel(final LayoutManager layout, final boolean isDoubleBuffered) {
    super(layout, isDoubleBuffered);
  }

  protected AbstractPanel(final LayoutManager layout) {
    super(layout);
  }

  protected AbstractPanel(final boolean isDoubleBuffered) {
    super(isDoubleBuffered);
  }

  protected AbstractPanel() {
    super();
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    // NO-OP
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    // NO-OP
  }
}
