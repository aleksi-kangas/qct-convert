package com.github.aleksikangas.qct.ui;

import com.github.aleksikangas.qct.ui.file.QctFileServiceImpl;
import com.github.aleksikangas.qct.ui.image.ImageDisplayPanel;
import com.github.aleksikangas.qct.ui.settings.SettingsPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.HeadlessException;

public final class QctFrame extends JFrame {
  private final QctFileServiceImpl qctFileService = new QctFileServiceImpl();
  private final SettingsPanel settingsPanel = new SettingsPanel(qctFileService);
  private final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
  private final ImageDisplayPanel imageDisplayPanel = new ImageDisplayPanel(qctFileService);

  public QctFrame() throws HeadlessException {
    super("QCT Convert");

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new MigLayout("", "[fill, grow]", "[fill, grow]"));

    splitPane.setResizeWeight(0.2);
    splitPane.setLeftComponent(new JScrollPane(settingsPanel));
    splitPane.setRightComponent(imageDisplayPanel);
    add(splitPane);
    pack();
  }
}
