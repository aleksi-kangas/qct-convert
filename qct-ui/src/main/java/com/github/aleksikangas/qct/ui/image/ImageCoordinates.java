package com.github.aleksikangas.qct.ui.image;

import com.google.common.base.Preconditions;

final class ImageCoordinates extends AbstractCoordinates {
  ImageCoordinates(final ImageDisplayState state, final double y, final double x) {
    super(state, y, x);
    Preconditions.checkArgument(state.image == null || (0 <= y && y < state.image.getHeight()));
    Preconditions.checkArgument(state.image == null || (0 <= x && x < state.image.getWidth()));
  }

  ImageCoordinates(final PanelCoordinates panelCoordinates) {
    super(panelCoordinates.state,
          (panelCoordinates.y - panelCoordinates.state.yOrigin) / panelCoordinates.state.imageScale,
          (panelCoordinates.x - panelCoordinates.state.xOrigin) / panelCoordinates.state.imageScale);
  }

  ImageCoordinates(final MinimapCoordinates minimapCoordinates) {
    super(minimapCoordinates.state,
          minimapCoordinates.y * minimapCoordinates.state.imageHeight() / minimapCoordinates.state.minimapHeight,
          minimapCoordinates.x * minimapCoordinates.state.imageWidth() / minimapCoordinates.state.minimapWidth);
  }


}
