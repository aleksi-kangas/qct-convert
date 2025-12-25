package com.github.aleksikangas.qct.ui.export;

import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportRequestEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class ExportPanel extends AbstractPanel {
  private final JButton exportGeoTiffButton = new JButton("GeoTiff...");
  private final JButton exportPngButton = new JButton("PNG...");

  private final transient Controller controller;

  @Nullable
  private transient Path decodedQctFilePath = null;

  public ExportPanel(final Controller controller) {
    super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[grow][grow]", "[fill, grow]"));
    this.controller = Objects.requireNonNull(controller);
    setBorder(BorderFactory.createTitledBorder("Export"));
    enableButtons(false);
    exportGeoTiffButton.addActionListener(_ -> exportAsGeoTiff());
    add(exportGeoTiffButton);
    exportPngButton.addActionListener(_ -> exportAsPng());
    add(exportPngButton, "alignx right, wrap");
  }

  @Override
  public void onDecodeRequest(final DecodeRequestEvent event) {
    enableButtons(false);
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    decodedQctFilePath = event.qctFile().path();
    enableButtons(true);
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    decodedQctFilePath = null;
    enableButtons(false);
  }

  @Override
  public void onExportSuccess(final ExportSuccessEvent event) {
    enableButtons(true);
  }

  @Override
  public void onExportFailure(final ExportFailureEvent event) {
    enableButtons(true);
  }

  private void exportAsGeoTiff() {
    Preconditions.checkNotNull(decodedQctFilePath);
    selectExportPath(ExportFormat.GEO_TIFF).ifPresent(exportPath -> {
      exportGeoTiffButton.setEnabled(false);
      controller.export(ExportFormat.GEO_TIFF, exportPath);
    });
  }

  private void exportAsPng() {
    Preconditions.checkNotNull(decodedQctFilePath);
    selectExportPath(ExportFormat.PNG).ifPresent(exportPath -> {
      exportPngButton.setEnabled(false);
      controller.export(ExportFormat.PNG, exportPath);
    });
  }

  private Optional<Path> selectExportPath(final ExportFormat exportFormat) {
    Preconditions.checkNotNull(decodedQctFilePath);
    final var fileChooser = new JFileChooser();
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    fileChooser.setFileFilter(new FileNameExtensionFilter(exportFormat.toString(), exportFormat.extension()));
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setSelectedFile(getSuggestedPath(decodedQctFilePath, exportFormat).toFile());
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      return Optional.of(fileChooser.getSelectedFile().toPath());
    }
    return Optional.empty();
  }

  private static Path getSuggestedPath(final Path qctFilePath, final ExportFormat exportFormat) {
    return qctFilePath.getParent()
                      .resolve(qctFilePath.getFileName()
                                          .toString()
                                          .replace(".qct", "")
                                          .replace(".QCT", "") + exportFormat.extension());
  }

  private void enableButtons(final boolean enabled) {
    exportGeoTiffButton.setEnabled(enabled);
    exportPngButton.setEnabled(enabled);
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<ExportPanel> {
    private final Event<ExportRequestEvent> exportRequestEventPublisher;

    @Inject
    public Controller(final Event<ExportRequestEvent> exportRequestEventPublisher) {
      this.exportRequestEventPublisher = Objects.requireNonNull(exportRequestEventPublisher);
    }

    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new ExportPanel(this));
    }

    private void export(final ExportFormat exportFormat, final Path exportPath) {
      exportRequestEventPublisher.fire(new ExportRequestEvent(exportFormat, exportPath));
    }
  }
}