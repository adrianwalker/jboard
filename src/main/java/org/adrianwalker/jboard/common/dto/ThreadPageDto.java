package org.adrianwalker.jboard.common.dto;

import java.io.Serializable;
import java.util.List;

public final class ThreadPageDto implements Serializable {

  private List<PostDto> posts;

  public List<PostDto> getPosts() {
    return posts;
  }

  public void setPosts(final List<PostDto> posts) {
    this.posts = posts;
  }

  @Override
  public String toString() {
    return "ThreadPageDto{" + "posts=" + posts.size() + '}';
  }
}
