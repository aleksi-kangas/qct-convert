package com.github.aleksikangas.qct.core.parser.task;

import java.nio.channels.AsynchronousFileChannel;

public interface AsyncReadable {
  AsynchronousFileChannel asyncFileChannel();
}
