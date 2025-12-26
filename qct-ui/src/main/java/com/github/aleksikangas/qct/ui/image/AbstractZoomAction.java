package com.github.aleksikangas.qct.ui.image;

import javax.swing.*;
import java.util.Objects;

abstract class AbstractZoomAction extends AbstractAction {
  static final double ZOOM_HIGH_QUALITY_THRESHOLD = 10.0;

  protected static final double ZOOM_MIN = 0.33;
  protected static final double ZOOM_MAX = 20.0;
  protected static final double ZOOM_STEP = 0.2;

  protected final ImageDisplayPanel panel;
  protected final ImageDisplayState state;

  AbstractZoomAction(final ImageDisplayPanel panel, final ImageDisplayState state) {
    this.panel = Objects.requireNonNull(panel);
    this.state = Objects.requireNonNull(state);
  }

  protected final void zoomImage(final double zoomFactor) {
    final var centerPanelCoordinates = new PanelCoordinates(state, panel.getHeight() / 2.0, panel.getWidth() / 2.0);
    final var centerImageCoordinates = new ImageCoordinates(centerPanelCoordinates);
    state.imageScale *= zoomFactor;
    state.imageScale = Math.max(ZOOM_MIN * state.imageFitScale, Math.min(ZOOM_MAX * state.imageFitScale, state.imageScale));
    final var newCenterPanelCoordinates = new PanelCoordinates(centerImageCoordinates);
    state.yOrigin += centerPanelCoordinates.yAsInt() - newCenterPanelCoordinates.yAsInt();
    state.xOrigin += centerPanelCoordinates.xAsInt() - newCenterPanelCoordinates.xAsInt();
    panel.repaint();
  }
}
