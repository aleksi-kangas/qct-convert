package com.github.aleksikangas.qct.ui.image;

import com.github.aleksikangas.qct.core.image.util.ImageIndexUtils;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ImageDisplayPanel extends AbstractPanel {
  private final ImageDisplayState state = new ImageDisplayState();

  private final transient Controller controller;

  public ImageDisplayPanel(final Controller controller) {
    super(new MigLayout("insets 4", "[fill, grow]", "[fill, grow][fill]"));
    this.controller = Objects.requireNonNull(controller);
    getInputMap().put(KeyStroke.getKeyStroke(ZoomInAction.KEY_CODE, 0), ZoomInAction.KEY);
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), ZoomOutAction.KEY);
    getActionMap().put(ZoomInAction.KEY, new ZoomInAction(this, state));
    getActionMap().put(ZoomOutAction.KEY, new ZoomOutAction(this, state));
    addMouseListener(new MinimapMouseAdapter(this, state));
    addMouseMotionListener(new PanMouseMotionListener(this, state));
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(800, 800);
  }

  @Override
  public void onDecodeRequest(final DecodeRequestEvent event) {
    state.clear();
    repaint();
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    CompletableFuture.supplyAsync(() -> ImageIndexUtils.asBufferedImage(event.qctFile()))
                     .thenAccept(image -> ThreadUtil.runOnEDT(() -> {
                       state.setImage(image, getHeight(), getWidth());
                       repaint();
                     }));
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    state.clear();
    repaint();
  }

  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);
    if (state.image == null) {
      return;
    }
    paintImage(g);
    paintMinimap(g);
  }

  private void paintImage(final Graphics g) {
    Objects.requireNonNull(state.image);
    if (state.currentZoom() > AbstractZoomAction.ZOOM_HIGH_QUALITY_THRESHOLD) {
      displayedImageRectangle().filter(r -> r.height > 0 && r.width > 0)
                               .ifPresent(r -> {
                                 final BufferedImage subBufferedImage = state.image.getSubimage(r.x,
                                                                                                r.y,
                                                                                                r.width,
                                                                                                r.height);
                                 final var g2d = (Graphics2D) g;
                                 g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                                      RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                 g2d.drawImage(subBufferedImage,
                                               Math.max(0, state.xOrigin),
                                               Math.max(0, state.yOrigin),
                                               Math.min(getWidth(),
                                                        (int) (subBufferedImage.getWidth() * state.imageScale)),
                                               Math.min(getHeight(),
                                                        (int) (subBufferedImage.getHeight() * state.imageScale)),
                                               null);
                               });
    } else {
      g.drawImage(state.image, state.xOrigin, state.yOrigin, state.imageWidth(), state.imageHeight(), null);
    }
  }

  private void paintMinimap(final Graphics g) {
    g.drawImage(state.minimapImage, 0, 0, state.minimapWidth, state.minimapHeight, null);
    g.setColor(Color.BLACK);
    g.drawRect(0, 0, state.minimapWidth, state.minimapHeight);
    if (!state.isWholeImageDisplayed(getHeight(), getWidth())) {
      g.setColor(Color.RED);
      g.drawRect(-state.xOrigin * state.minimapWidth / state.imageWidth(),
                 -state.yOrigin * state.minimapHeight / state.imageHeight(),
                 getWidth() * state.minimapWidth / state.imageWidth(),
                 getHeight() * state.minimapHeight / state.imageHeight());
    }
  }


  private Optional<Rectangle> displayedImageRectangle() {
    if (state.image == null) {
      return Optional.empty();
    }
    final var topLeftImageCoordinates = new ImageCoordinates(new PanelCoordinates(state, 0, 0));
    final var bottomRightImageCoordinates = new ImageCoordinates(new PanelCoordinates(state,
                                                                                      getHeight() - 1,
                                                                                      getWidth() - 1));
    if (state.image.getHeight() <= topLeftImageCoordinates.y || bottomRightImageCoordinates.y < 0 ||
        state.image.getWidth() <= topLeftImageCoordinates.x || bottomRightImageCoordinates.x < 0) {
      return Optional.empty();
    }
    final int y1 = Math.max(0, topLeftImageCoordinates.yAsInt());
    final int x1 = Math.max(0, topLeftImageCoordinates.xAsInt());
    final int y2 = Math.min(state.image.getHeight() - 1, bottomRightImageCoordinates.yAsInt());
    final int x2 = Math.min(state.image.getWidth() - 1, bottomRightImageCoordinates.xAsInt());
    return Optional.of(new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1));
  }

  @ApplicationScoped
  public static class Controller extends AbstractController<ImageDisplayPanel> {
    @PostConstruct
    public void init() {
      ThreadUtil.runOnEDT(() -> panel = new ImageDisplayPanel(this));
    }
  }
}
