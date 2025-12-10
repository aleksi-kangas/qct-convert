package com.github.aleksikangas.qct.ui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import javax.swing.SwingUtilities;

@ApplicationScoped
public class QctApplication {
  private QctFrame qctFrame;

  public void start(final @Observes ContainerInitialized containerInitialized) {
    FlatMacDarkLaf.setup();
    SwingUtilities.invokeLater(() -> {
      qctFrame = new QctFrame();
      qctFrame.setLocationRelativeTo(null);
      qctFrame.setVisible(true);
    });
  }

  static void main(final String[] args) {
    StartMain.main(args);
  }
}
