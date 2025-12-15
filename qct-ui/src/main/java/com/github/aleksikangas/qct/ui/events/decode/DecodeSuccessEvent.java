package com.github.aleksikangas.qct.ui.events.decode;

import com.github.aleksikangas.qct.core.QctFile;

public record DecodeSuccessEvent(QctFile qctFile) {
  public interface Aware {
    void onDecodeSuccess(DecodeSuccessEvent event);
  }
}
