package com.github.aleksikangas.qct.ui.image;

import com.github.aleksikangas.qct.core.image.ImageUtils;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.annotation.Nullable;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class ImageDisplayPanel extends AbstractPanel {
  private final transient Controller controller;

  @Nullable
  private transient BufferedImage bufferedImage = null;

  public ImageDisplayPanel(final Controller controller) {
    super(new MigLayout("", "[fill, grow]", "[fill, grow][fill]"));
    this.controller = Objects.requireNonNull(controller);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1000, 1000);
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    CompletableFuture.supplyAsync(() -> ImageUtils.asBufferedImage(event.qctFile().imageIndex().asPixels()))
        .thenAccept(
            image -> ThreadUtil.runOnEDT(() -> {
              this.bufferedImage = image;
              repaint();
            }));
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    this.bufferedImage = null;
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (bufferedImage == null || bufferedImage.getWidth() == 0 || bufferedImage.getHeight() == 0 || getWidth() == 0 || getHeight() == 0) {
      return;
    }
    final double scaleY = (double) getHeight() / bufferedImage.getHeight();
    final double scaleX = (double) getWidth() / bufferedImage.getWidth();
    final double scale = Math.min(scaleY, scaleX);

    final int scaledHeight = (int) (bufferedImage.getHeight() * scale);
    final int scaledWidth = (int) (bufferedImage.getWidth() * scale);

    final int y = (getHeight() - scaledHeight) / 2;
    final int x = (getWidth() - scaledWidth) / 2;

    final Graphics2D g2d = (Graphics2D) g;
    try {
      g2d.setRenderingHints(Map.of(RenderingHints.KEY_ANTIALIASING,
                                   RenderingHints.VALUE_ANTIALIAS_ON,
                                   RenderingHints.KEY_INTERPOLATION,
                                   RenderingHints.VALUE_INTERPOLATION_BILINEAR));
      g2d.drawImage(bufferedImage,
                    x,
                    y,
                    x + scaledWidth,
                    y + scaledHeight,
                    0,
                    0,
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    this);
    } finally {
      g2d.dispose();
    }
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<ImageDisplayPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new ImageDisplayPanel(this));
    }
  }
}
