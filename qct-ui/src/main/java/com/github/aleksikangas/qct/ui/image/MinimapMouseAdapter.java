package com.github.aleksikangas.qct.ui.image;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

final class MinimapMouseAdapter extends MouseAdapter {
  private final ImageDisplayPanel panel;
  private final ImageDisplayState state;

  MinimapMouseAdapter(final ImageDisplayPanel panel, final ImageDisplayState state) {
    this.panel = Objects.requireNonNull(panel);
    this.state = Objects.requireNonNull(state);
  }

  @Override
  public void mousePressed(final MouseEvent e) {
    if (SwingUtilities.isLeftMouseButton(e) && state.isWithinMinimap(e.getPoint())) {
      final var imageCoordinates = new ImageCoordinates(new MinimapCoordinates(state, e.getY(), e.getX()));
      state.yOrigin = panel.getHeight() / 2 - imageCoordinates.yAsInt();
      state.xOrigin = panel.getWidth() / 2 - imageCoordinates.xAsInt();
      panel.repaint();
    }
  }
}
