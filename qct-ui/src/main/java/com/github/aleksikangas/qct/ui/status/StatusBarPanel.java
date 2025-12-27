package com.github.aleksikangas.qct.ui.status;

import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportFailureEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportRequestEvent;
import com.github.aleksikangas.qct.ui.events.export.ExportSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;

public final class StatusBarPanel extends AbstractPanel {
  private final transient Controller controller;

  private final JLabel statusLabel = new JLabel();
  private final JProgressBar progressBar = new JProgressBar();

  private boolean isDecoding = false;
  private boolean isExporting = false;

  public StatusBarPanel(final Controller controller) {
    super(new MigLayout("", "[][fill, grow]", "[fill, grow]"));
    this.controller = Objects.requireNonNull(controller);
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);
    add(statusLabel);
    add(progressBar);
  }

  @Override
  public void onDecodeRequest(final DecodeRequestEvent event) {
    isDecoding = true;
    updateStatusLabel();
    updateProgressBar();
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    isDecoding = false;
    updateStatusLabel();
    updateProgressBar();
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    isDecoding = false;
    updateStatusLabel();
    updateProgressBar();
  }

  @Override
  public void onExportRequest(final ExportRequestEvent event) {
    isExporting = true;
    updateStatusLabel();
    updateProgressBar();
  }

  @Override
  public void onExportFailure(final ExportFailureEvent event) {
    isExporting = false;
    updateStatusLabel();
    updateProgressBar();
  }

  @Override
  public void onExportSuccess(final ExportSuccessEvent event) {
    isExporting = false;
    updateStatusLabel();
    updateProgressBar();
  }

  private void updateStatusLabel() {
    if (isDecoding) {
      statusLabel.setText("Decoding...");
    }
    else if (isExporting) {
      statusLabel.setText("Exporting...");
    }
    else {
      statusLabel.setText("");
    }
  }

  private void updateProgressBar() {
    progressBar.setVisible(isDecoding || isExporting);
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<StatusBarPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new StatusBarPanel(this));
    }
  }
}
