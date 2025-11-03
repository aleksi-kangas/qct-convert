package com.github.aleksikangas.qct.core.reader;

import javax.annotation.concurrent.NotThreadSafe;
import java.nio.channels.AsynchronousFileChannel;
import java.util.Objects;

/**
 * A dynamic wrapper for accessing bytes from a file one by one in a more performant way.
 *
 * @implNote Reads multiple (= {@link #capacity}) bytes into memory, serving {@link #nextByte()} requests. When the
 * requested byte offset is not server by the buffer in memory, a {@link #capacity} of bytes is read from the file,
 * replacing the internal buffer.
 */
@NotThreadSafe
public final class DynamicByteBuffer {
  private final AsynchronousFileChannel asyncFileChannel;
  private final int capacity;

  /**
   * The buffer.
   */
  private int[] buffer = new int[0];
  /**
   * The current file byte offset of the start of the {@link #buffer}.
   */
  private long bufferStartFileByteOffset;
  /**
   * The current index of {@link #nextByte()} in the {@link #buffer}.
   */
  private int bufferIndex = 0;

  public DynamicByteBuffer(final AsynchronousFileChannel asyncFileChannel, long byteOffset, final int initialCapacity) {
    this.asyncFileChannel = Objects.requireNonNull(asyncFileChannel);
    this.capacity = initialCapacity;
    this.bufferStartFileByteOffset = byteOffset;
    readBytes();
  }

  /**
   * Returns the next byte. Advances buffer offset by one after reading, i.e. next invocation of this method shall
   * return the byte after the previously returned one.
   *
   * @return the next byte
   * @apiNote May read additional bytes from the file if necessary to accommodate the request.
   */
  public int nextByte() {
    if (bufferIndex < buffer.length) {
      return buffer[bufferIndex++];
    }
    bufferStartFileByteOffset += capacity;
    readBytes();
    return buffer[bufferIndex++];
  }

  private void readBytes() {
    buffer = QctReader.readBytesSafe(asyncFileChannel, bufferStartFileByteOffset, capacity);
    bufferIndex = 0;
  }
}
