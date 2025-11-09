package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.Objects;

final class DatumShiftPanel extends JPanel implements QctFileAware {
  private final QctFileService qctFileService;

  private final JLabel northLabel = new JLabel("North:");
  private final JTextField northField = new JTextField();

  private final JLabel eastLabel = new JLabel("East:");
  private final JTextField eastField = new JTextField();

  DatumShiftPanel(final QctFileService qctFileService) {
    super(new MigLayout("insets 0", "[][fill, grow]", "[][]"));
    this.qctFileService = Objects.requireNonNull(qctFileService);

    northField.setEnabled(false);
    eastField.setEnabled(false);

    add(northLabel);
    add(northField, "wrap");

    add(eastLabel);
    add(eastField, "wrap");
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
    northField.setText(String.valueOf(qctFile.metadata().extendedData().datumShift().north()));
    eastField.setText(String.valueOf(qctFile.metadata().extendedData().datumShift().east()));
  }
}
