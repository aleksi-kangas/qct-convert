package com.github.aleksikangas.qct.ui;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jboss.weld.environment.se.StartMain;
import org.jboss.weld.environment.se.events.ContainerInitialized;

import java.util.Objects;

@ApplicationScoped
public class QctApplication {
  private final QctFrame.Controller qctFrameController;

  @Inject
  public QctApplication(final QctFrame.Controller qctFrameController) {
    this.qctFrameController = Objects.requireNonNull(qctFrameController);
  }

  public void start(final @Observes ContainerInitialized containerInitialized) {
    qctFrameController.show();
  }

  static void main(final String[] args) {
    FlatMacDarkLaf.setup();
    StartMain.main(args);
  }
}
