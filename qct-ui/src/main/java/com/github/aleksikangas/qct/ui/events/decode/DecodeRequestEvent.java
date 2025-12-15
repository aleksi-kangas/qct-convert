package com.github.aleksikangas.qct.ui.events.decode;

import java.nio.file.Path;

public record DecodeRequestEvent(Path qctFilePath) {
  public interface Aware {
    void onDecodeRequest(DecodeRequestEvent event);
  }
}
