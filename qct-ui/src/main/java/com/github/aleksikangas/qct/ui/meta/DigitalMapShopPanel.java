package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.meta.DigitalMapShop;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.Objects;

public final class DigitalMapShopPanel extends AbstractPanel {
  private final JTextField sizeField = new JTextField();
  private final JTextField qc3UrlField = new JTextField();

  private final transient Controller controller;

  public DigitalMapShopPanel(final Controller controller) {
    super(new MigLayout("insets 0", "[][fill, grow]", "[][]"));
    this.controller = Objects.requireNonNull(controller);

    sizeField.setEnabled(false);
    qc3UrlField.setEnabled(false);

    add(new JLabel("Size:"));
    add(sizeField, "wrap");

    add(new JLabel("QC3 URL:"));
    add(qc3UrlField, "wrap");
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    final DigitalMapShop digitalMapShop = event.qctFile().metadata().extendedData().digitalMapShop();
    sizeField.setText(String.valueOf(digitalMapShop.size()));
    qc3UrlField.setText(digitalMapShop.qc3Url());
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    sizeField.setText("");
    qc3UrlField.setText("");
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<DigitalMapShopPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new DigitalMapShopPanel(this));
    }
  }
}
