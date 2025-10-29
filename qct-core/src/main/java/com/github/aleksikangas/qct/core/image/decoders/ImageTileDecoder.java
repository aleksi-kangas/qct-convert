package com.github.aleksikangas.qct.core.image.decoders;

import com.github.aleksikangas.qct.core.image.ImageTile;
import com.github.aleksikangas.qct.core.image.ImageTileEncoding;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.nio.channels.AsynchronousFileChannel;

public interface ImageTileDecoder {
  /**
   * A decoder for {@link ImageTile}s using the returned {@link ImageTileEncoding}.
   *
   * @return applicable {@link ImageTileEncoding}
   */
  ImageTileEncoding decoderFor();

  /**
   * Decodes a compressed {@link ImageTile}.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       byte offset of the {@link ImageTile}
   * @return decoded pixels of the {@link ImageTile}
   */
  @Nonnull
  Color[][] decode(AsynchronousFileChannel asyncFileChannel, long byteOffset);
}
