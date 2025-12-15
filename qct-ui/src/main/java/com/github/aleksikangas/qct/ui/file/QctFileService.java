package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.QctRuntimeException;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@ApplicationScoped
public final class QctFileService implements DecodeRequestEvent.Aware {
  private static final Logger LOG = LoggerFactory.getLogger(QctFileService.class);

  private final Event<DecodeFailureEvent> decodeFailureEventPublisher;
  private final Event<DecodeSuccessEvent> decodeSuccessEventPublisher;

  @Inject
  public QctFileService(final Event<DecodeFailureEvent> decodeFailureEventPublisher,
                        final Event<DecodeSuccessEvent> decodeSuccessEventPublisher) {
    this.decodeFailureEventPublisher = Objects.requireNonNull(decodeFailureEventPublisher);
    this.decodeSuccessEventPublisher = Objects.requireNonNull(decodeSuccessEventPublisher);
  }

  @Override
  public void onDecodeRequest(@ObservesAsync final DecodeRequestEvent e) {
    try {
      final QctFile qctFile = QctFile.parse(e.qctFilePath());
      decodeSuccessEventPublisher.fireAsync(new DecodeSuccessEvent(qctFile));
    } catch (final QctRuntimeException ex) {
      LOG.warn("Failed to parse QctFile", ex);
      decodeFailureEventPublisher.fireAsync(new DecodeFailureEvent(ex));
    }
  }
}
