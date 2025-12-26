package com.github.aleksikangas.qct.ui.image;

import com.github.aleksikangas.qct.core.image.util.ImageIndexUtils;
import com.github.aleksikangas.qct.ui.common.AbstractController;
import com.github.aleksikangas.qct.ui.common.AbstractPanel;
import com.github.aleksikangas.qct.ui.events.decode.DecodeFailureEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeRequestEvent;
import com.github.aleksikangas.qct.ui.events.decode.DecodeSuccessEvent;
import com.github.aleksikangas.qct.ui.util.ThreadUtil;
import com.google.common.base.Preconditions;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class ImageDisplayPanel extends AbstractPanel {
  private static final double IMAGE_ZOOM_INCREMENT = 0.2;
  private static final double MINIMAP_SIZE_FACTOR = 0.2;
  private static final double ZOOM_MIN = 0.33;
  private static final double ZOOM_MAX = 20.0;
  private static final double ZOOM_HIGH_QUALITY_THRESHOLD = 7.5;

  private final transient Controller controller;

  @Nullable
  private transient BufferedImage image = null;
  private double imageFitScale = 0.0;
  private double imageScale = 0.0;
  private int yOrigin = 0;
  private int xOrigin = 0;

  @Nullable
  private transient BufferedImage minimapImage = null;
  private int minimapHeight = 0;
  private int minimapWidth = 0;

  public ImageDisplayPanel(final Controller controller) {
    super(new MigLayout("debug", "[fill, grow]", "[fill, grow][fill]"));
    this.controller = Objects.requireNonNull(controller);
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "ZOOM_IN");
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "ZOOM_OUT");
    getActionMap().put("ZOOM_IN", new AbstractAction() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        zoomImage(1.0 + IMAGE_ZOOM_INCREMENT);
      }
    });
    getActionMap().put("ZOOM_OUT", new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        zoomImage(1.0 - IMAGE_ZOOM_INCREMENT);
      }
    });
    addMouseMotionListener(new MouseMotionListener() {
      @Nullable
      private Point mousePosition = null;

      @Override
      public void mouseDragged(final MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && !isWithinMinimap(e.getPoint())) {
          Objects.requireNonNull(mousePosition);
          final Point panPosition = e.getPoint();
          yOrigin += panPosition.y - mousePosition.y;
          xOrigin += panPosition.x - mousePosition.x;
          mousePosition = panPosition;
          repaint();
        }
      }

      @Override
      public void mouseMoved(final MouseEvent e) {
        mousePosition = e.getPoint();
      }
    });
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(final MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && isWithinMinimap(e.getPoint())) {
          centerImageAt(new MinimapCoordinates(e.getY(), e.getX()));
        }
      }
    });
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1000, 1000);
  }

  @Override
  public void onDecodeRequest(final DecodeRequestEvent event) {
    clear();
  }

  @Override
  public void onDecodeSuccess(final DecodeSuccessEvent event) {
    CompletableFuture.supplyAsync(() -> ImageIndexUtils.asBufferedImage(event.qctFile()))
                     .thenAccept(image -> ThreadUtil.runOnEDT(() -> {
                       this.image = image;
                       imageFitScale = Math.min((double) getHeight() / image.getHeight(),
                                                (double) getWidth() / image.getWidth());
                       imageScale = imageFitScale;
                       yOrigin = (getHeight() - imageHeight()) / 2;
                       xOrigin = (getWidth() - imageWidth()) / 2;

                       minimapHeight = (int) (getHeight() * MINIMAP_SIZE_FACTOR);
                       minimapWidth = minimapHeight * image.getWidth() / image.getHeight();
                       minimapImage = new BufferedImage(minimapWidth, minimapHeight, image.getType());
                       minimapImage.getGraphics().drawImage(image, 0, 0, minimapWidth, minimapHeight, null);

                       repaint();
                     }));
  }

  @Override
  public void onDecodeFailure(final DecodeFailureEvent event) {
    clear();
  }

  @Override
  protected void paintComponent(final Graphics g) {
    super.paintComponent(g);
    if (image == null) {
      return;
    }
    paintImage(g);
    paintMinimap(g);
  }

  private void paintImage(final Graphics g) {
    Objects.requireNonNull(image);
    if (currentZoom() > ZOOM_HIGH_QUALITY_THRESHOLD) {
      displayedImageRectangle().filter(r -> r.height > 0 && r.width > 0)
                               .ifPresent(r -> {
                                 final BufferedImage subBufferedImage = image.getSubimage(r.x, r.y, r.width, r.height);
                                 final var g2d = (Graphics2D) g;
                                 g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                                      RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                 g2d.drawImage(subBufferedImage,
                                               Math.max(0, xOrigin),
                                               Math.max(0, yOrigin),
                                               Math.min(getWidth(), (int) (subBufferedImage.getWidth() * imageScale)),
                                               Math.min(getHeight(), (int) (subBufferedImage.getHeight() * imageScale)),
                                               null);
                               });
    } else {
      g.drawImage(image, xOrigin, yOrigin, imageWidth(), imageHeight(), null);
    }
  }

  private void paintMinimap(final Graphics g) {
    g.drawImage(minimapImage, 0, 0, minimapWidth, minimapHeight, null);
    g.setColor(Color.BLACK);
    g.drawRect(0, 0, minimapWidth, minimapHeight);
    if (!isWholeImageDisplayed()) {
      g.setColor(Color.RED);
      g.drawRect(-xOrigin * minimapWidth / imageWidth(),
                 -yOrigin * minimapHeight / imageHeight(),
                 getWidth() * minimapWidth / imageWidth(),
                 getHeight() * minimapHeight / imageHeight());
    }
  }

  private void zoomImage(final double zoomFactor) {
    final var centerPanelCoordinates = new PanelCoordinates(getHeight() / 2.0, getWidth() / 2.0);
    final var centerImageCoordinates = new ImageCoordinates(centerPanelCoordinates);
    imageScale *= zoomFactor;
    imageScale = Math.max(ZOOM_MIN * imageFitScale, Math.min(ZOOM_MAX * imageFitScale, imageScale));
    final var newCenterPanelCoordinates = new PanelCoordinates(centerImageCoordinates);
    yOrigin += centerPanelCoordinates.yAsInt() - newCenterPanelCoordinates.yAsInt();
    xOrigin += centerPanelCoordinates.xAsInt() - newCenterPanelCoordinates.xAsInt();
    repaint();
  }

  private void centerImageAt(final MinimapCoordinates minimapCoordinates) {
    final var imageCoordinates = new ImageCoordinates(minimapCoordinates);
    yOrigin = getHeight() / 2 - imageCoordinates.yAsInt();
    xOrigin = getWidth() / 2 - imageCoordinates.xAsInt();
    repaint();
  }

  private void clear() {
    image = null;
    imageFitScale = 0.0;
    imageScale = 0.0;
    yOrigin = 0;
    xOrigin = 0;

    minimapImage = null;
    minimapHeight = 0;
    minimapWidth = 0;

    repaint();
  }

  private int imageHeight() {
    if (image == null) {
      return 0;
    }
    return (int) (imageScale * image.getHeight());
  }

  private int imageWidth() {
    if (image == null) {
      return 0;
    }
    return (int) (imageScale * image.getWidth());
  }

  private double currentZoom() {
    return imageScale / imageFitScale;
  }

  private boolean isWholeImageDisplayed() {
    return 0 <= yOrigin && (yOrigin + imageHeight()) <= getHeight() &&
        0 <= xOrigin && (xOrigin + imageWidth()) <= getWidth();
  }

  private boolean isWithinMinimap(final Point point) {
    return 0 <= point.y && point.y < minimapHeight &&
        0 <= point.x && point.x < minimapWidth;
  }

  private Optional<Rectangle> displayedImageRectangle() {
    if (image == null) {
      return Optional.empty();
    }
    final ImageCoordinates topLeft = new ImageCoordinates(new PanelCoordinates(0, 0));
    final ImageCoordinates bottomRight = new ImageCoordinates(new PanelCoordinates(getHeight() - 1, getWidth() - 1));
    if (image.getHeight() <= topLeft.y || bottomRight.y < 0 ||
        image.getWidth() <= topLeft.x || bottomRight.x < 0) {
      return Optional.empty();
    }
    final int y1 = Math.max(0, topLeft.yAsInt());
    final int x1 = Math.max(0, topLeft.xAsInt());
    final int y2 = Math.min(image.getHeight() - 1, bottomRight.yAsInt());
    final int x2 = Math.min(image.getWidth() - 1, bottomRight.xAsInt());
    return Optional.of(new Rectangle(x1, y1, x2 - x1 + 1, y2 - y1 + 1));
  }

  public final class PanelCoordinates {
    private final double y;
    private final double x;

    public PanelCoordinates(final double y, final double x) {
      Preconditions.checkArgument(0 <= y && y < getHeight());
      Preconditions.checkArgument(0 <= x && x < getWidth());
      this.y = y;
      this.x = x;
    }

    public PanelCoordinates(final ImageCoordinates imageCoordinates) {
      Objects.requireNonNull(imageCoordinates);
      y = imageCoordinates.y * imageScale + yOrigin;
      x = imageCoordinates.x * imageScale + xOrigin;
    }

    public int yAsInt() {
      return (int) Math.round(y);
    }

    public int xAsInt() {
      return (int) Math.round(x);
    }
  }

  public final class ImageCoordinates {
    private final double y;
    private final double x;

    public ImageCoordinates(final double y, final double x) {
      Preconditions.checkArgument(image == null || (0 <= y && y < image.getHeight()));
      Preconditions.checkArgument(image == null || (0 <= x && x < image.getWidth()));
      this.y = y;
      this.x = x;
    }

    public ImageCoordinates(final PanelCoordinates panelCoordinates) {
      Objects.requireNonNull(panelCoordinates);
      y = (panelCoordinates.y - yOrigin) / imageScale;
      x = (panelCoordinates.x - xOrigin) / imageScale;
    }

    public ImageCoordinates(final MinimapCoordinates minimapCoordinates) {
      Objects.requireNonNull(minimapCoordinates);
      y = minimapCoordinates.y * imageHeight() / minimapHeight;
      x = minimapCoordinates.x * imageWidth() / minimapWidth;
    }

    public int yAsInt() {
      return (int) Math.round(y);
    }

    public int xAsInt() {
      return (int) Math.round(x);
    }
  }

  public final class MinimapCoordinates {
    private final double y;
    private final double x;

    public MinimapCoordinates(final double y, final double x) {
      Preconditions.checkArgument(0 <= y && y < minimapHeight);
      Preconditions.checkArgument(0 <= x && x < minimapWidth);
      this.y = y;
      this.x = x;
    }

    public int yAsInt() {
      return (int) Math.round(y);
    }

    public int xAsInt() {
      return (int) Math.round(x);
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
