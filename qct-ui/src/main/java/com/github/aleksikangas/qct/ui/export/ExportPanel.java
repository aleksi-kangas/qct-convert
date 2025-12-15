package com.github.aleksikangas.qct.ui.export;

import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.util.Objects;

public final class ExportPanel extends AbstractPanel implements DecodeSuccessEvent.Aware, DecodeFailureEvent.Aware {
  private final JButton exportPngButton = new JButton("PNG...");

  private final transient Controller controller;

  public ExportPanel(final Controller controller) {
    super(new MigLayout("fill, insets 4 10 4 10, gap 10", "[grow]", "[fill, grow]"));
    this.controller = Objects.requireNonNull(controller);
    setBorder(BorderFactory.createTitledBorder("Export"));
    exportPngButton.setEnabled(false);
    add(exportPngButton, "wrap");
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    exportPngButton.setEnabled(true);
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    exportPngButton.setEnabled(false);
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<ExportPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new ExportPanel(this));
    }
  }
}
