package com.github.aleksikangas.qct.core.color;

import java.awt.Color;

/**
 * Representation of pixel in a {@link com.github.aleksikangas.qct.core.QctFile}.
 */
public record QctPixel(Color color,
                       int paletteIndex) {
}
