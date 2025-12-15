package com.github.aleksikangas.qct.ui.util;

import com.github.aleksikangas.qct.ui.common.QctApplicationRuntimeException;

import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

public final class ThreadUtil {
  public static void runOnEDT(final Runnable runnable) {
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      try {
        SwingUtilities.invokeAndWait(runnable);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new QctApplicationRuntimeException(e);
      } catch (final InvocationTargetException e) {
        throw new QctApplicationRuntimeException(e);
      }
    }
  }

  private ThreadUtil() {
  }
}
