package com.github.aleksikangas.qct.ui;

import com.github.aleksikangas.qct.ui.file.QctFileService;
import com.github.aleksikangas.qct.ui.image.ImageDisplayPanel;
import com.github.aleksikangas.qct.ui.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class QctFrame extends JFrame {
  public QctFrame(final QctFileService qctFileService) throws HeadlessException {
    super("QCT Convert");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new MigLayout("insets 0", "[fill, grow]", "[fill, grow]"));

    final var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(0.3);
    final var scrollPane = new JScrollPane(new SettingsPanel(qctFileService));
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    splitPane.setLeftComponent(scrollPane);
    splitPane.setRightComponent(new ImageDisplayPanel(qctFileService));
    add(splitPane);
    pack();
  }
}
