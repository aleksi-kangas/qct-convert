package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.core.QctFile;

import java.nio.file.Path;

public interface QctFileService {
  void bind(QctFileAware qctFileAware);

  void unbind(QctFileAware qctFileAware);

  void decodeQctFile(final Path path);

  void scaleQctFile(final QctFile qctFile, final int scale);
}
