package com.github.aleksikangas.qct.ui.image;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

final class ZoomOutAction extends AbstractZoomAction {
  static final String KEY = "ZOOM_OUT";
  static final int KEY_CODE = KeyEvent.VK_MINUS;

  ZoomOutAction(final ImageDisplayPanel panel, final ImageDisplayState state) {
    super(panel, state);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    zoomImage(1.0 - ZOOM_STEP);
  }
}
