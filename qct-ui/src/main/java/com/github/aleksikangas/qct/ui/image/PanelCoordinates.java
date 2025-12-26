package com.github.aleksikangas.qct.ui.image;

final class PanelCoordinates extends AbstractCoordinates {
  PanelCoordinates(final ImageDisplayState state, final double y, final double x) {
    super(state, y, x);
  }

  PanelCoordinates(final ImageCoordinates imageCoordinates) {
    super(imageCoordinates.state,
          imageCoordinates.y * imageCoordinates.state.imageScale + imageCoordinates.state.yOrigin,
          imageCoordinates.x * imageCoordinates.state.imageScale + imageCoordinates.state.xOrigin);
  }
}
