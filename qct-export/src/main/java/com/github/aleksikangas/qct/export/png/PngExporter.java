package com.github.aleksikangas.qct.export.png;

import ar.com.hjg.pngj.FilterType;
import com.github.aleksikangas.qct.core.QctFile;
import com.github.aleksikangas.qct.core.image.util.ImageIndexUtils;
import com.github.aleksikangas.qct.export.QctExportRuntimeException;
import it.geosolutions.imageio.plugins.png.PNGWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class PngExporter {
  public static void exportPng(final QctFile qctFile, final Path pngPath) throws QctExportRuntimeException {
    final var pngWriter = new PNGWriter();
    try {
      pngWriter.writePNG(ImageIndexUtils.asBufferedImage(qctFile),
                         Files.newOutputStream(pngPath),
                         1.0f,
                         FilterType.FILTER_NONE);
    } catch (final Exception e) {
      throw new QctExportRuntimeException(e);
    }
  }

  public static CompletableFuture<Void> exportPngAsync(final QctFile qctFile, final Path pngPath) {
    return CompletableFuture.runAsync(() -> exportPng(qctFile, pngPath));
  }

  private PngExporter() {
  }
}
