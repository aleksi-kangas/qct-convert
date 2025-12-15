package com.github.aleksikangas.qct.ui.events.decode;

public record DecodeFailureEvent(Throwable throwable) {
  public interface Aware {
    void onDecodeFailure(DecodeFailureEvent event);
  }
}
