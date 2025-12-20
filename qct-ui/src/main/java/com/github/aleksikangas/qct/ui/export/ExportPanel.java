package com.github.aleksikangas.qct.ui.export;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public final class ExportPanel extends AbstractPanel {
  private final JButton exportPngButton = new JButton("PNG...");

  private final transient Controller controller;

  @Nullable
  private transient QctFile decodedQctFile = null;

  public ExportPanel(final Controller controller) {
    super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[grow]", "[fill, grow]"));
    this.controller = Objects.requireNonNull(controller);
    setBorder(BorderFactory.createTitledBorder("Export"));
    exportPngButton.setEnabled(false);
    exportPngButton.addActionListener(_ -> exportAsPng());
    add(exportPngButton, "wrap");
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    decodedQctFile = event.qctFile();
    exportPngButton.setEnabled(true);
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    decodedQctFile = null;
    exportPngButton.setEnabled(false);
  }

  @Override
  public void onExportSuccess(final ExportSuccessEvent event) {
    exportPngButton.setEnabled(true);
  }

  @Override
  public void onExportFailure(final ExportFailureEvent event) {
    exportPngButton.setEnabled(true);
  }

  private void exportAsPng() {
    Preconditions.checkNotNull(decodedQctFile);
    selectPngExportPath().ifPresent(pngExportPath -> {
      exportPngButton.setEnabled(false);
      controller.export(ExportFormat.PNG, pngExportPath);
    });
  }

  private Optional<Path> selectPngExportPath() {
    Preconditions.checkNotNull(decodedQctFile);
    final var fileChooser = new JFileChooser();
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
    fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    fileChooser.setMultiSelectionEnabled(false);
    final Path qctFilePath = decodedQctFile.path();
    final Path suggestedPngPath = qctFilePath.getParent().resolve(qctFilePath.getFileName().toString().replace(".qct",
                                                                                                               "") + ".png");
    fileChooser.setSelectedFile(suggestedPngPath.toFile());
    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
      return Optional.of(fileChooser.getSelectedFile().toPath());
    }
    return Optional.empty();
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
      exportRequestEventPublisher.fireAsync(new ExportRequestEvent(exportFormat, exportPath));
    }
  }
}