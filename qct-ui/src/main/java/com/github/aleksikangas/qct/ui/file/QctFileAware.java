package com.github.aleksikangas.qct.ui.file;

import com.github.aleksikangas.qct.core.QctFile;

public interface QctFileAware {
  /**
   * @param qctFile the {@link QctFile}
   * @apiNote Guaranteed to be called from EDT.
   */
  void onQctFile(QctFile qctFile);
}
