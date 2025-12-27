package com.github.aleksikangas.qct.ui.export;

import com.github.aleksikangas.qct.export.geotiff.GeoTiffExporter;
import com.github.aleksikangas.qct.export.png.PngExporter;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportRequestEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class QctExportService implements ExportRequestEvent.Aware {
  private static final Logger LOG = LoggerFactory.getLogger(QctExportService.class);

  private final Event<ExportFailureEvent> exportFailureEventPublisher;
  private final Event<ExportSuccessEvent> exportSuccessEventPublisher;
  private final QctFileService qctFileService;

  @Inject
  public QctExportService(final Event<ExportFailureEvent> exportFailureEventPublisher,
                          final Event<ExportSuccessEvent> exportSuccessEventPublisher,
                          final QctFileService qctFileService) {
    this.exportFailureEventPublisher = Objects.requireNonNull(exportFailureEventPublisher);
    this.exportSuccessEventPublisher = Objects.requireNonNull(exportSuccessEventPublisher);
    this.qctFileService = Objects.requireNonNull(qctFileService);
  }

  @Override
  public void onExportRequest(@Observes final ExportRequestEvent event) {
    qctFileService.getQctFile()
        .ifPresent(qctFile -> {
          CompletableFuture.runAsync(() -> {
            switch (event.exportFormat()) {
              case GEO_TIFF -> {
                GeoTiffExporter.exportGeoTiff(qctFile, ensureExtension(event.exportFormat(), event.exportPath()));
                LOG.info("GeoTIFF export successful: {}", event.exportPath());
                exportSuccessEventPublisher.fire(new ExportSuccessEvent());
              }
              case PNG -> {
                PngExporter.exportPng(qctFile, ensureExtension(event.exportFormat(), event.exportPath()));
                LOG.info("PNG export successful: {}", event.exportPath());
                exportSuccessEventPublisher.fire(new ExportSuccessEvent());
              }
            }
          }).exceptionally(ex -> {
            LOG.warn("Export failed", ex);
            exportFailureEventPublisher.fire(new ExportFailureEvent(ex));
            return null;
          });
        });
  }

  private static Path ensureExtension(final ExportFormat exportFormat, final Path exportPath) {
    String absolutePath = exportPath.toAbsolutePath().toString();
    if (!absolutePath.endsWith(exportFormat.extension())) {
      absolutePath += exportFormat.extension();
    }
    return Path.of(absolutePath);
  }
}
