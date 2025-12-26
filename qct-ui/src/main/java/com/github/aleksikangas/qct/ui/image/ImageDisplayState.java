package com.github.aleksikangas.qct.ui.image;

import jakarta.annotation.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

final class ImageDisplayState {
  static final double MINIMAP_SIZE_FACTOR = 0.2;

  @Nullable
  BufferedImage image = null;
  double imageFitScale = 0.0;
  double imageScale = 0.0;
  int yOrigin = 0;
  int xOrigin = 0;

  @Nullable
  BufferedImage minimapImage = null;
  int minimapHeight = 0;
  int minimapWidth = 0;

  void setImage(final BufferedImage image, final int availableHeight, final int availableWidth) {
    this.image = image;
    imageFitScale = Math.min((double) availableHeight / image.getHeight(),
                             (double) availableWidth / image.getWidth());
    imageScale = imageFitScale;
    yOrigin = (availableHeight - imageHeight()) / 2;
    xOrigin = (availableWidth - imageWidth()) / 2;

    minimapHeight = (int) (availableHeight * MINIMAP_SIZE_FACTOR);
    minimapWidth = minimapHeight * image.getWidth() / image.getHeight();
    minimapImage = new BufferedImage(minimapWidth, minimapHeight, image.getType());
    minimapImage.getGraphics().drawImage(image, 0, 0, minimapWidth, minimapHeight, null);

  }

  int imageHeight() {
    return (int) (imageScale * (image != null ? image.getHeight() : 0));
  }

  int imageWidth() {
    return (int) (imageScale * (image != null ? image.getWidth() : 0));
  }

  double currentZoom() {
    return imageScale / imageFitScale;
  }

  boolean isWholeImageDisplayed(final int availableHeight, final int availableWidth) {
    return 0 <= yOrigin && (yOrigin + imageHeight()) <= availableHeight &&
        0 <= xOrigin && (xOrigin + imageWidth()) <= availableWidth;
  }

  boolean isWithinMinimap(final Point point) {
    return 0 <= point.y && point.y < minimapHeight && 0 <= point.x && point.x < minimapWidth;
  }

  void clear() {
    image = null;
    imageFitScale = 0.0;
    imageScale = 0.0;
    yOrigin = 0;
    xOrigin = 0;

    minimapImage = null;
    minimapHeight = 0;
    minimapWidth = 0;
  }
}
