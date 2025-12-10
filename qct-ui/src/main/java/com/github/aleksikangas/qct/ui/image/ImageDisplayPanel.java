package com.github.aleksikangas.qct.ui.image;

import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.image.ImageUtils;
import com.github.aleksikangas.qct.ui.file.QctFileAware;
import com.github.aleksikangas.qct.ui.file.QctFileService;
import net.miginfocom.swing.MigLayout;

import javax.annotation.Nullable;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class ImageDisplayPanel extends JPanel implements QctFileAware {
  private final QctFileService qctFileService;

  @Nullable
  private BufferedImage bufferedImage;

  public ImageDisplayPanel(final QctFileService qctFileService) {
    super(new MigLayout("", "[fill, grow]", "[fill, grow][fill]"));
    this.qctFileService = Objects.requireNonNull(qctFileService);
  }

  @Override
  public void addNotify() {
    super.addNotify();
    qctFileService.bind(this);
  }

  @Override
  public void removeNotify() {
    super.removeNotify();
    qctFileService.unbind(this);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(1000, 1000);
  }

  @Override
  public void onQctFile(final QctFile qctFile) {
    CompletableFuture.supplyAsync(() -> ImageUtils.asBufferedImage(qctFile.imageIndex().asPixels())).thenAccept(
        bufferedImage -> SwingUtilities.invokeLater(() -> {
          this.bufferedImage = bufferedImage;
          revalidate();
          repaint();
        }));
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
}
