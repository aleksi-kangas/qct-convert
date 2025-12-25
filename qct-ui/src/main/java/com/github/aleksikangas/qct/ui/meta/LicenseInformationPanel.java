package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.meta.LicenseInformation;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;

public final class LicenseInformationPanel extends AbstractPanel {
  private final JTextField identifierField = new JTextField();
  private final JTextField descriptionField = new JTextField();
  private final JTextField serialNumberField = new JTextField();

  private final transient Controller controller;

  public LicenseInformationPanel(final Controller controller) {
    super(new MigLayout("insets 0", "[][fill, grow]", "[][][]"));
    this.controller = Objects.requireNonNull(controller);

    identifierField.setEnabled(false);
    descriptionField.setEnabled(false);
    serialNumberField.setEnabled(false);

    add(new JLabel("Identifier:"));
    add(identifierField, "wrap");

    add(new JLabel("Description:"));
    add(descriptionField, "wrap");

    add(new JLabel("Serial Number:"));
    add(serialNumberField, "wrap");
  }

  @Override
  public void onDecodeRequest(final DecodeRequestEvent event) {
    clear();
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    final LicenseInformation licenseInformation = event.qctFile().metadata().extendedData().licenseInformation();
    identifierField.setText(String.valueOf(licenseInformation.identifier()));
    descriptionField.setText(licenseInformation.description());
    serialNumberField.setText(licenseInformation.serialNumber().toString());
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    clear();
  }

  private void clear() {
    identifierField.setText("");
    descriptionField.setText("");
    serialNumberField.setText("");
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<LicenseInformationPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new LicenseInformationPanel(this));
    }
  }
}
