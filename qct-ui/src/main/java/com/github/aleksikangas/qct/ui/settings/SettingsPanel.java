package com.github.aleksikangas.qct.ui.settings;

import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.export.ExportPanel;
import com.github.aleksikangas.qct.ui.file.FilePanel;
import com.github.aleksikangas.qct.ui.meta.MetadataPanel;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Objects;

public final class SettingsPanel extends AbstractPanel implements Scrollable {
  private final transient Controller controller;

  public SettingsPanel(final Controller controller,
                       final FilePanel filePanel,
                       final ExportPanel exportPanel,
                       final MetadataPanel metadataPanel) {
    super(new MigLayout("fill, insets 4", "[fill, grow]", "[][fill, grow]"));
    this.controller = Objects.requireNonNull(controller);

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
    if (getParent() instanceof JViewport viewport && viewport.getParent() instanceof JScrollPane scrollPane) {
      preferredSize.width += scrollPane.getVerticalScrollBar().getPreferredSize().width;
    }

    return preferredSize;
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<SettingsPanel> {
    private final FilePanel.Controller filePanelController;
    private final ExportPanel.Controller exportPanelController;
    private final MetadataPanel.Controller metadataPanelController;

    @Inject
    public Controller(final FilePanel.Controller filePanelController,
                      final ExportPanel.Controller exportPanelController,
                      final MetadataPanel.Controller metadataPanelController) {
      this.filePanelController = Objects.requireNonNull(filePanelController);
      this.exportPanelController = Objects.requireNonNull(exportPanelController);
      this.metadataPanelController = Objects.requireNonNull(metadataPanelController);
    }

    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new SettingsPanel(this,
                                                          filePanelController.getPanel(),
                                                          exportPanelController.getPanel(),
                                                          metadataPanelController.getPanel()));
    }
  }
}
