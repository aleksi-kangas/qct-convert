package com.github.aleksikangas.qct.ui.status;

import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;

public final class StatusBarPanel extends AbstractPanel {
  private final transient Controller controller;

  private final JLabel statusLabel = new JLabel();
  private final JProgressBar progressBar = new JProgressBar();

  public StatusBarPanel(final Controller controller) {
    super(new MigLayout("", "[grow][fill, grow]", "[fill, grow]"));
    this.controller = Objects.requireNonNull(controller);
    progressBar.setIndeterminate(true);
    progressBar.setVisible(false);
    add(statusLabel);
    add(progressBar);
  }

  @Override
  public void onDecodeRequest(final DecodeRequestEvent event) {
    statusLabel.setText("Decoding...");
    progressBar.setVisible(true);
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    clear();
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    clear();
  }

  private void clear() {
    statusLabel.setText("");
    progressBar.setVisible(false);
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<StatusBarPanel> implements DecodeRequestEvent.Aware {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new StatusBarPanel(this));
    }

    @Override
    public void onDecodeRequest(@ObservesAsync final DecodeRequestEvent event) {
      SwingUtilities.invokeLater(() -> panel.onDecodeRequest(event));
    }
  }
}
