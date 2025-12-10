package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.core.QctFile;

import javax.swing.SwingUtilities;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public final class QctFileServiceImpl implements QctFileService {
  private final Set<QctFileAware> listeners = new CopyOnWriteArraySet<>();

  private final AtomicReference<QctFile> qctFile = new AtomicReference<>(null);

  @Override
  public void bind(final QctFileAware qctFileAware) {
    listeners.add(Objects.requireNonNull(qctFileAware));
  }

  @Override
  public void unbind(final QctFileAware qctFileAware) {
    listeners.remove(qctFileAware);
  }

  @Override
  public void decodeQctFile(final Path path) {
    CompletableFuture.supplyAsync(() -> QctFile.parse(Objects.requireNonNull(path))).thenAccept(qctFile -> {
      this.qctFile.set(qctFile);
      notifyQctFileListeners();
    }).exceptionally(e -> {
      System.out.println(e);
      return null;
    });
  }

  @Override
  public void scaleQctFile(final QctFile qctFile, final int scale) {

  }

  private void notifyQctFileListeners() {
    final QctFile currentQctFile = qctFile.get();
    SwingUtilities.invokeLater(() -> listeners.forEach(l -> l.onQctFile(currentQctFile)));
  }
}
