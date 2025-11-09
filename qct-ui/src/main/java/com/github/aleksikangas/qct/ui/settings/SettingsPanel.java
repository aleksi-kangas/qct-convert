package com.github.aleksikangas.qct.ui.settings;

import com.github.aleksikangas.qct.ui.file.FilePanel;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import com.github.aleksikangas.qct.ui.meta.MetadataPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JPanel;

public final class SettingsPanel extends JPanel {
  private final FilePanel filePanel;
  private final MetadataPanel metadataPanel;

  public SettingsPanel(final QctFileService qctFileService) {
    super(new MigLayout("", "[fill, grow]", "[][fill, grow]"));
    filePanel = new FilePanel(qctFileService);
    metadataPanel = new MetadataPanel(qctFileService);

    add(filePanel, "wrap");
    add(metadataPanel, "wrap");
  }
}
