package com.github.aleksikangas.qct.ui;

import com.github.aleksikangas.qct.ui.image.ImageDisplayPanel;
import com.github.aleksikangas.qct.ui.settings.SettingsPanel;
import com.github.aleksikangas.qct.ui.status.StatusBarPanel;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.Objects;

public final class QctFrame extends JFrame {
  private final transient Controller controller;

  public QctFrame(final Controller controller,
                  final SettingsPanel settingsPanel,
                  final ImageDisplayPanel imageDisplayPanel,
                  final StatusBarPanel statusBarPanel) {
    super("QCT Convert");
    this.controller = Objects.requireNonNull(controller);

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new MigLayout("insets 0", "[fill, grow]", "[fill, grow][grow]"));

    final var splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    splitPane.setResizeWeight(0.3);
    final var scrollPane = new JScrollPane(settingsPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    splitPane.setLeftComponent(scrollPane);
    splitPane.setRightComponent(imageDisplayPanel);
    add(splitPane, "wrap");
    add(statusBarPanel);
    pack();
  }

  @ApplicationScoped
  public static class Controller {
    private final SettingsPanel.Controller settingsPanelController;
    private final ImageDisplayPanel.Controller imageDisplayPanelController;
    private final StatusBarPanel.Controller statusBarPanelController;

    @Inject
    public Controller(final SettingsPanel.Controller settingsPanelController,
                      final ImageDisplayPanel.Controller imageDisplayPanelController,
                      final StatusBarPanel.Controller statusBarPanelController) {
      this.settingsPanelController = Objects.requireNonNull(settingsPanelController);
      this.imageDisplayPanelController = Objects.requireNonNull(imageDisplayPanelController);
      this.statusBarPanelController = Objects.requireNonNull(statusBarPanelController);
    }

    private QctFrame qctFrame;

    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> qctFrame = new QctFrame(this,
                                                        settingsPanelController.getPanel(),
                                                        imageDisplayPanelController.getPanel(),
                                                        statusBarPanelController.getPanel()));
    }

    public void show() {
      ThreadUtil.runOnEDT(() -> {
        qctFrame.setLocationRelativeTo(null);
        qctFrame.setVisible(true);
      });
    }
  }
}
