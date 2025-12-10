package com.github.aleksikangas.qct.core.parser.feature;

import java.nio.channels.AsynchronousFileChannel;

public interface AsyncFileChannelAware {
  AsynchronousFileChannel asyncFileChannel();
}
