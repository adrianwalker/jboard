package org.adrianwalker.jboard.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

  private CommonUtil() {
  }

  public static File[] readDirs(final File directory) {
    File[] dirs = directory.listFiles(new FileFilter() {

      @Override
      public boolean accept(final File file) {
        return null == file ? false : file.isDirectory();
      }
    });

    if (null == dirs) {
      dirs = new File[0];
    }

    return dirs;
  }

  public static void sortDirs(final File[] directories, final boolean latestFirst) {
    Arrays.sort(directories, new LastModifiedComparator(latestFirst));
  }

  private static class LastModifiedComparator implements Comparator<File> {

    private boolean latestFirst;

    public LastModifiedComparator(final boolean latestFirst) {
      this.latestFirst = latestFirst;
    }

    @Override
    public int compare(final File f1, final File f2) {
      if (latestFirst) {
        return Long.valueOf(f2.lastModified()).compareTo(Long.valueOf(f1.lastModified()));
      } else {
        return Long.valueOf(f1.lastModified()).compareTo(Long.valueOf(f2.lastModified()));
      }
    }
  }

  public static void prune(final File dir, final int max, final boolean latestFirst) throws IOException, InterruptedException {
    File[] files = CommonUtil.readDirs(dir);
    if (files.length > max) {
      CommonUtil.sortDirs(files, latestFirst);
      int remove = files.length - max;

      for (int i = 0; i < remove; i++) {

        LOGGER.info(String.format("pruning %s", files[i]));
        CommonUtil.deleteDir(files[i]);
      }
    }
  }

  public static boolean deleteDir(final File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }

    return dir.delete();
  }

  public static int exec(final String[] command) throws IOException, InterruptedException {
    Process proc = Runtime.getRuntime().exec(command);
    proc.waitFor();
    return proc.exitValue();
  }
}
