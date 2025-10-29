package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.QctException;
import com.github.aleksikangas.qct.core.image.ImageTile;

/**
 * A checked {@link QctException} indicating an issue in decoding of an {@link ImageTile}.
 *
 * @apiNote These exceptions should be handled and e.g. replace the tile with completely black pixel values.
 */
public final class QctDecoderException extends QctException {
  QctDecoderException(final String message, final Throwable cause) {
    super(message, cause);
  }

  QctDecoderException(final Throwable cause) {
    super(cause);
  }

  QctDecoderException(final String message) {
    super(message);
  }
}
