package com.github.aleksikangas.qct.ui.export;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.export.QctExportRuntimeException;
import com.github.aleksikangas.qct.export.png.PngExporter;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportRequestEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public final class QctExportService implements DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware, ExportRequestEvent.Aware {
  private static final Logger LOG = LoggerFactory.getLogger(QctExportService.class);

  private final Event<ExportFailureEvent> exportFailureEventPublisher;
  private final Event<ExportSuccessEvent> exportSuccessEventPublisher;

  private final AtomicReference<QctFile> decodedQctFile = new AtomicReference<>(null);

  @Inject
  public QctExportService(final Event<ExportFailureEvent> exportFailureEventPublisher, final Event<ExportSuccessEvent> exportSuccessEventPublisher) {
    this.exportFailureEventPublisher = Objects.requireNonNull(exportFailureEventPublisher);
    this.exportSuccessEventPublisher = Objects.requireNonNull(exportSuccessEventPublisher);
  }

  @Override
  public void onDecodeFailure(@ObservesAsync final DecodeFailureEvent event) {
    decodedQctFile.set(null);
  }

  @Override
  public void onDecodeSuccess(@ObservesAsync final DecodeSuccessEvent event) {
    decodedQctFile.set(event.qctFile());
  }

  @Override
  public void onExportRequest(@ObservesAsync final ExportRequestEvent event) {
    final QctFile qctFile = decodedQctFile.get();
    if (qctFile == null) {
      LOG.warn("Export failed, QCT file was null");
      return;
    }
    try {
      switch (event.exportFormat()) {
        case PNG -> {
          PngExporter.exportPng(qctFile, ensureExtension(event.exportFormat(), event.exportPath()));
          exportSuccessEventPublisher.fireAsync(new ExportSuccessEvent());
        }
      }
    } catch (final QctExportRuntimeException e) {
      exportFailureEventPublisher.fireAsync(new ExportFailureEvent(e));
    }
  }

  private static Path ensureExtension(final ExportFormat exportFormat, final Path exportPath) {
    String absolutePath = exportPath.toAbsolutePath().toString();
    if (!absolutePath.endsWith(exportFormat.extension())) {
      absolutePath += exportFormat.extension();
    }
    return Path.of(absolutePath);
  }
}
