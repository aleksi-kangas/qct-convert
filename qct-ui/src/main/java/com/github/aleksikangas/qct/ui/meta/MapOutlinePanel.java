package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.meta.MapOutline;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.Objects;

public final class MapOutlinePanel extends AbstractPanel {
  private final transient Controller controller;

  public MapOutlinePanel(final Controller controller) {
    super(new MigLayout("insets 0", "[][fill, grow]", ""));
    this.controller = Objects.requireNonNull(controller);
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    removeAll();
    for (final MapOutline.Point point : event.qctFile().metadata().mapOutline().points()) {
      final var pointLabel = new JLabel("Lat / Lon (°):");

      final JTextField pointField = new JTextField(point.latitude() + " / " + point.longitude());
      pointField.setEnabled(false);

      add(pointLabel);
      add(pointField, "wrap");
    }
    revalidate();
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    removeAll();
    revalidate();
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<MapOutlinePanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new MapOutlinePanel(this));
    }
  }
}
