package com.github.aleksikangas.qct.ui.meta;

import com.github.aleksikangas.qct.core.meta.DatumShift;
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

public final class DatumShiftPanel extends AbstractPanel {
  private final JTextField northField = new JTextField();
  private final JTextField eastField = new JTextField();

  private final transient Controller controller;

  public DatumShiftPanel(final Controller controller) {
    super(new MigLayout("insets 0", "[][fill, grow]", "[][]"));
    this.controller = Objects.requireNonNull(controller);

    northField.setEnabled(false);
    eastField.setEnabled(false);

    add(new JLabel("North:"));
    add(northField, "wrap");

    add(new JLabel("East:"));
    add(eastField, "wrap");
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    final DatumShift datumShift = event.qctFile().metadata().extendedData().datumShift();
    northField.setText(String.valueOf(datumShift.north()));
    eastField.setText(String.valueOf(datumShift.east()));
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    northField.setText("");
    eastField.setText("");
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<DatumShiftPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new DatumShiftPanel(this));
    }
  }
}
