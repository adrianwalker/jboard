package org.adrianwalker.jboard.common.dto;

import java.io.Serializable;
import java.util.List;

public final class BoardPageDto implements Serializable {

  private List<ThreadDto> threads;
  private int page;
  private int pages;

  public int getPage() {
    return page;
  }

  public void setPage(final int page) {
    this.page = page;
  }

  public int getPages() {
    return pages;
  }

  public void setPages(final int pages) {
    this.pages = pages;
  }

  public List<ThreadDto> getThreads() {
    return threads;
  }

  public void setThreads(List<ThreadDto> threads) {
    this.threads = threads;
  }

  @Override
  public String toString() {
    return "BoardPageDto{" + "threads=" + threads.size() + ", page=" + page + ", pages=" + pages + '}';
  }
}
