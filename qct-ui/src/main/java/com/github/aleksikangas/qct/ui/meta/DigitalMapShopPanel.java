package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Objects;

final class DigitalMapShopPanel extends JPanel implements QctFileAware {
  private final QctFileService qctFileService;

  private final JLabel sizeLabel = new JLabel("Size:");
  private final JTextField sizeField = new JTextField();

  private final JLabel qc3UrlLabel = new JLabel("QC3 URL:");
  private final JTextField qc3UrlField = new JTextField();

  DigitalMapShopPanel(final QctFileService qctFileService) {
    super(new MigLayout("insets 0", "[][fill, grow]", "[][]"));
    this.qctFileService = Objects.requireNonNull(qctFileService);

    sizeField.setEnabled(false);
    qc3UrlField.setEnabled(false);

    add(sizeLabel);
    add(sizeField, "wrap");

    add(qc3UrlLabel);
    add(qc3UrlField, "wrap");
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
    sizeField.setText(String.valueOf(qctFile.metadata().extendedData().digitalMapShop().size()));
    qc3UrlField.setText(qctFile.metadata().extendedData().digitalMapShop().qc3Url());
  }
}
