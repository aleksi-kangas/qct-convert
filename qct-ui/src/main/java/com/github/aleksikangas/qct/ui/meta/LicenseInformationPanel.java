package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Objects;

final class LicenseInformationPanel extends JPanel implements QctFileAware {
  private final QctFileService qctFileService;

  private final JLabel identifierLabel = new JLabel("Identifier:");
  private final JTextField identifierField = new JTextField();

  private final JLabel descriptionLabel = new JLabel("Description:");
  private final JTextField descriptionField = new JTextField();

  private final JLabel serialNumberLabel = new JLabel("Serial Number:");
  private final JTextField serialNumberField = new JTextField();

  LicenseInformationPanel(final QctFileService qctFileService) {
    super(new MigLayout("insets 0", "[][fill, grow]", "[][][]"));
    this.qctFileService = Objects.requireNonNull(qctFileService);

    identifierField.setEnabled(false);
    descriptionField.setEnabled(false);
    serialNumberField.setEnabled(false);

    add(identifierLabel);
    add(identifierField, "wrap");

    add(descriptionLabel);
    add(descriptionField, "wrap");

    add(serialNumberLabel);
    add(serialNumberField, "wrap");
  }

  @Override
  public void addNotify() {
    super.addNotify();
    qctFileService.bind(this);
  }

  @Override
  public void removeNotify() {
    super.removeNotify();
    qctFileService.unbind(this);
  }

  @Override
  public void onQctFile(final QctFile qctFile) {
    identifierField.setText(String.valueOf(qctFile.metadata().extendedData().licenseInformation().identifier()));
    descriptionField.setText(qctFile.metadata().extendedData().licenseInformation().description());
    serialNumberField.setText(qctFile.metadata().extendedData().licenseInformation().serialNumber().toString());
  }
}
