package com.github.aleksikangas.qct.core.reader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;

public final class QctReader {
  /**
   * Reads a single unsigned byte from the given byte offset.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset of the byte
   * @return read byte
   */
  public static int readByte(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
    return readBytes(asyncFileChannel, byteOffset, 1)[0];
  }

  /**
   * Reads multiple unsigned bytes from the given byte offset.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset of the first byte
   * @param count            bytes to read
   * @return read bytes
   */
  public static int[] readBytes(final AsynchronousFileChannel asyncFileChannel,
                                final long byteOffset,
                                final int count) {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
    try {
      if (asyncFileChannel.read(byteBuffer, byteOffset).get() == count) {
        final int[] bytes = new int[count];
        for (int i = 0; i < count; ++i) {
          bytes[i] = byteBuffer.get(i) & 0xFF;
        }
        return bytes;
      } else {
        throw new QctReaderException(String.format("Failed to read %d bytes from: %d", count, byteOffset));
      }
    } catch (final ExecutionException e) {
      throw new QctReaderException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctReaderException(e);
    }
  }

  /**
   * Reads multiple unsigned bytes from the given byte offset, until count or EOF.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset of the first byte
   * @param count            bytes to read
   * @return read bytes, until count or EOF
   */
  public static int[] readBytesSafe(final AsynchronousFileChannel asyncFileChannel,
                                    final long byteOffset,
                                    final int count) {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
    try {
      final int readCount = asyncFileChannel.read(byteBuffer, byteOffset).get();
      final int[] bytes = new int[readCount];
      for (int i = 0; i < readCount; ++i) {
        bytes[i] = byteBuffer.get(i) & 0xFF;
      }
      return bytes;
    } catch (final ExecutionException e) {
      throw new QctReaderException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctReaderException(e);
    }
  }

  /**
   * Reads a double (8 byte IEEE-754) from the given byte offset.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset of the double
   * @return read double
   */
  public static double readDouble(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    try {
      if (asyncFileChannel.read(byteBuffer, byteOffset).get() == 8) {
        return byteBuffer.flip().getDouble();
      } else {
        throw new QctReaderException(String.format("Failed to read 8 byte double from: %d", byteOffset));
      }
    } catch (final ExecutionException e) {
      throw new QctReaderException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctReaderException(e);
    }
  }

  /**
   * Reads multiple doubles (8 byte IEEE-754) from the given byte offset.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       byte offset of the first double
   * @param count            doubles to read
   * @return read doubles
   */
  public static double[] readDoubles(final AsynchronousFileChannel asyncFileChannel,
                                     final long byteOffset,
                                     final int count) {
    final double[] doubles = new double[count];
    for (int i = 0; i < count; ++i) {
      doubles[i] = QctReader.readDouble(asyncFileChannel, byteOffset + i * 0x08L);
    }
    return doubles;
  }

  /**
   * Reads an integer (4-byte) stored as little-endian from the given byte offset.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset of the integer
   * @return read integer
   */
  public static int readInt(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
    try {
      if (asyncFileChannel.read(byteBuffer, byteOffset).get() == 4) {
        return byteBuffer.flip().getInt();
      } else {
        throw new QctReaderException(String.format("Failed to read 4 byte little endian integer from: %d", byteOffset));
      }
    } catch (final ExecutionException e) {
      throw new QctReaderException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctReaderException(e);
    }
  }

  /**
   * Reads an integer (4-byte) pointer stored as little-endian from the given byte offset. Pointers are essentially byte
   * offsets within the file.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       the byte offset of the pointer
   * @return read pointer
   * @see #readInt(AsynchronousFileChannel, long)
   */
  public static int readPointer(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
    return readInt(asyncFileChannel, byteOffset);
  }

  /**
   * Reads a NULL-terminated string from the given byte offset.
   *
   * @param asyncFileChannel to read from
   * @param byteOffset       byte offset of the string
   * @return read string
   */
  public static String readString(final AsynchronousFileChannel asyncFileChannel, final long byteOffset) {
    final ByteBuffer byteBuffer = ByteBuffer.allocate(1);
    final StringBuilder stringBuilder = new StringBuilder();
    try {
      while (asyncFileChannel.read(byteBuffer, byteOffset + stringBuilder.length()).get() == 1) {
        final byte b = byteBuffer.flip().get();
        if (b == 0) {
          return stringBuilder.toString();
        }
        final char c = (char) (b & 0xFF);  // ava bytes are signed -> perform unsigned conversion
        stringBuilder.append(c);
        byteBuffer.clear();
      }
      throw new QctReaderException(String.format("Failed to read string from: %d", byteOffset));
    } catch (final ExecutionException e) {
      throw new QctReaderException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new QctReaderException(e);
    }
  }

  /**
   * Reads a NULL-terminated string by first reading the string pointer from the given byte offset, and then reading the
   * string from the pointed byte offset.
   *
   * @param asyncFileChannel  to read from
   * @param pointerByteOffset byte offset of the string pointer
   * @return read string
   */
  public static String readStringFromPointer(final AsynchronousFileChannel asyncFileChannel,
                                             final long pointerByteOffset) {
    final int byteOffset = readPointer(asyncFileChannel, pointerByteOffset);
    if (byteOffset != 0) {
      return readString(asyncFileChannel, byteOffset);
    }
    return "";
  }

  private QctReader() {
  }
}
