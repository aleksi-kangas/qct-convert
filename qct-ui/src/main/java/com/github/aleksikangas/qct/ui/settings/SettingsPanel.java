package com.github.aleksikangas.qct.ui.settings;

import com.github.aleksikangas.qct.ui.export.ExportPanel;
import com.github.aleksikangas.qct.ui.file.FilePanel;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import com.github.aleksikangas.qct.ui.meta.MetadataPanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public final class SettingsPanel extends JPanel implements Scrollable {
  private final FilePanel filePanel;
  private final ExportPanel exportPanel;
  private final MetadataPanel metadataPanel;

  public SettingsPanel(final QctFileService qctFileService) {
    super(new MigLayout("fill, insets 4", "[fill, grow]", "[][fill, grow]"));
    filePanel = new FilePanel(qctFileService);
    exportPanel = new ExportPanel(qctFileService);
    metadataPanel = new MetadataPanel(qctFileService);

    add(filePanel, "wrap");
    add(exportPanel, "wrap");
    add(metadataPanel, "wrap");
  }

  @Override
  public Dimension getPreferredScrollableViewportSize() {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
    return 10;
  }

  @Override
  public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
    return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
  }

  @Override
  public boolean getScrollableTracksViewportWidth() {
    return true;
  }

  @Override
  public boolean getScrollableTracksViewportHeight() {
    return false;
  }

  @Override
  public Dimension getPreferredSize() {
    final Dimension preferredSize = super.getPreferredSize();
    if (getParent() instanceof JViewport viewport) {
      if (viewport.getParent() instanceof JScrollPane scrollPane) {
        preferredSize.width += scrollPane.getVerticalScrollBar().getPreferredSize().width;
      }
    }
    return preferredSize;
  }
}
