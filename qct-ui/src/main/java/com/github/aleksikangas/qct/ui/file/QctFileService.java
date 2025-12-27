package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class QctFileService implements DecodeRequestEvent.Aware {
  private static final Logger LOG = LoggerFactory.getLogger(QctFileService.class);

  private final Event<DecodeFailureEvent> decodeFailureEventPublisher;
  private final Event<DecodeSuccessEvent> decodeSuccessEventPublisher;

  private final AtomicReference<QctFile> qctFileAtomicReference = new AtomicReference<>(null);

  @Inject
  public QctFileService(final Event<DecodeFailureEvent> decodeFailureEventPublisher,
                        final Event<DecodeSuccessEvent> decodeSuccessEventPublisher) {
    this.decodeFailureEventPublisher = Objects.requireNonNull(decodeFailureEventPublisher);
    this.decodeSuccessEventPublisher = Objects.requireNonNull(decodeSuccessEventPublisher);
  }

  @Override
  public void onDecodeRequest(@Observes final DecodeRequestEvent e) {
    qctFileAtomicReference.set(null);
    CompletableFuture.supplyAsync(() -> QctFile.parse(e.qctFilePath()))
        .thenAccept(qctFile -> {
          LOG.info("QctFile decode successful: {}", e.qctFilePath());
          qctFileAtomicReference.set(qctFile);
          decodeSuccessEventPublisher.fire(new DecodeSuccessEvent(qctFile));
        })
        .exceptionally(ex -> {
          LOG.warn("Failed to decode QctFile: {}", e.qctFilePath(), ex);
          decodeFailureEventPublisher.fire(new DecodeFailureEvent(ex));
          return null;
        });
  }

  public Optional<QctFile> getQctFile() {
    return Optional.ofNullable(qctFileAtomicReference.get());
  }
}
