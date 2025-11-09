package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.meta.MapOutline;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class MapOutlinePanel extends JPanel implements QctFileAware {
  private final QctFileService qctFileService;

  private final List<JLabel> pointLabels = new ArrayList<>();
  private final List<JTextField> pointFields = new ArrayList<>();

  MapOutlinePanel(final QctFileService qctFileService) {
    super(new MigLayout("insets 0", "[][fill, grow]", ""));
    this.qctFileService = Objects.requireNonNull(qctFileService);
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
    pointLabels.clear();
    pointFields.clear();
    removeAll();
    for (final MapOutline.Point point : qctFile.metadata().mapOutline().points()) {
      final var pointLabel = new JLabel("Lat / Lon (°):");
      pointLabels.add(pointLabel);

      final JTextField pointField = new JTextField(point.latitude() + " / " + point.longitude());
      pointField.setEnabled(false);
      pointFields.add(pointField);

      add(pointLabel);
      add(pointField, "wrap");
    }
    revalidate();
    repaint();
  }
}
