package com.github.aleksikangas.qct.export.gdal;

import com.github.aleksikangas.qct.export.QctExportRuntimeException;
import org.gdal.gdal.gdal;

import javax.annotation.concurrent.GuardedBy;

public final class GdalRegistration {
  private static final Object REGISTRATION_LOCK = new Object();
  @GuardedBy("REGISTRATION_LOCK")
  private static boolean isRegistered = false;

  public static void register() {
    try {
      synchronized (REGISTRATION_LOCK) {
        if (!isRegistered) {
          gdal.AllRegister();
          isRegistered = true;
        }
      }
    } catch (final Throwable t) {
      throw new QctExportRuntimeException(t);
    }
  }

  private GdalRegistration() {
  }
}
