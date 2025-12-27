package com.github.aleksikangas.qct.ui.common;

import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportRequestEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractPanel extends JPanel
    implements DecodeRequestEvent.Aware, DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware,
    ExportRequestEvent.Aware, ExportSuccessEvent.Aware, ExportFailureEvent.Aware {
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
  public void onDecodeRequest(final DecodeRequestEvent event) {
    // NO-OP
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    // NO-OP
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    // NO-OP
  }

  @Override
  public void onExportRequest(final ExportRequestEvent event) {
    // NO-OP
  }

  @Override
  public void onExportFailure(final ExportFailureEvent event) {
    // NO-OP
  }

  @Override
  public void onExportSuccess(final ExportSuccessEvent event) {
    // NO-OP
  }
}
