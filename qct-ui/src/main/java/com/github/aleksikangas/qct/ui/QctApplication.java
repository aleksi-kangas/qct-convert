package com.github.aleksikangas.qct.ui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import javax.swing.*;
import java.util.Objects;

@ApplicationScoped
public class QctApplication {
  private final QctFileService qctFileService;

  @Inject
  public QctApplication(final QctFileService qctFileService) {
    this.qctFileService = Objects.requireNonNull(qctFileService);
  }

  public void start(final @Observes ContainerInitialized containerInitialized) {
    FlatMacDarkLaf.setup();
    SwingUtilities.invokeLater(() -> {
      final var qctFrame = new QctFrame(qctFileService);
      qctFrame.setLocationRelativeTo(null);
      qctFrame.setVisible(true);
    });
  }

  static void main(final String[] args) {
    StartMain.main(args);
  }
}
