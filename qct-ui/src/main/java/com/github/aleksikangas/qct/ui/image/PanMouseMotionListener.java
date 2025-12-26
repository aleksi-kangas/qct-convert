package com.github.aleksikangas.qct.ui.image;

import jakarta.annotation.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Objects;

final class PanMouseMotionListener implements MouseMotionListener {
  private final ImageDisplayPanel panel;
  private final ImageDisplayState state;

  @Nullable
  private Point mousePosition = null;

  PanMouseMotionListener(final ImageDisplayPanel panel, final ImageDisplayState state) {
    this.panel = Objects.requireNonNull(panel);
    this.state = Objects.requireNonNull(state);
  }

  @Override
  public void mouseDragged(final MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e) && !state.isWithinMinimap(e.getPoint())) {
      Objects.requireNonNull(mousePosition);
      final Point panPosition = e.getPoint();
      state.yOrigin += panPosition.y - mousePosition.y;
      state.xOrigin += panPosition.x - mousePosition.x;
      mousePosition = panPosition;
      panel.repaint();
    }
  }

  @Override
  public void mouseMoved(final MouseEvent e) {
    mousePosition = e.getPoint();
  }
}
