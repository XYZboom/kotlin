// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.openapi.projectRoots.impl;

import com.intellij.openapi.projectRoots.JdkUtil;
import com.intellij.util.ArrayUtilRt;
import com.intellij.util.ObjectUtils;
import com.intellij.util.SystemProperties;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

abstract class JavaHomeFinderBase {
  @NotNull
  public abstract List<String> findExistingJdks();

  protected void scanFolder(@NotNull File folder, boolean includeNestDirs, @NotNull List<? super String> result) {
    if (JdkUtil.checkForJdk(folder)) {
      result.add(folder.getAbsolutePath());
    }
    else if (includeNestDirs) {
      for (File file : ObjectUtils.notNull(folder.listFiles(), ArrayUtilRt.EMPTY_FILE_ARRAY)) {
        file = adjustPath(file);
        if (JdkUtil.checkForJdk(file)) {
          result.add(file.getAbsolutePath());
        }
      }
    }
  }

  protected File adjustPath(File file) {
    return file;
  }

  protected static File getJavaHome() {
    String property = SystemProperties.getJavaHome();
    if (property == null)
      return null;

    File javaHome = new File(property).getParentFile();//actually java.home points to to jre home
    return javaHome == null || !javaHome.isDirectory() ? null : javaHome;
  }
}
