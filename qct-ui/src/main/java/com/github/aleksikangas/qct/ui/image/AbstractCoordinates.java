package com.github.aleksikangas.qct.ui.image;

import java.util.Objects;

abstract class AbstractCoordinates {
  protected final ImageDisplayState state;
  protected final double y;
  protected final double x;

  AbstractCoordinates(final ImageDisplayState state,
                      final double y,
                      final double x) {
    this.state = Objects.requireNonNull(state);
    this.y = y;
    this.x = x;
  }

  final int yAsInt() {
    return (int) Math.round(y);
  }

  final int xAsInt() {
    return (int) Math.round(x);
  }
}
