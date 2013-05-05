package org.adrianwalker.jboard.common.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class ThreadDto implements Serializable {

  private String uuid;
  private Date date;
  private PostDto firstPost;
  private List<PostDto> lastPosts;

  public ThreadDto() {
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(final String uuid) {
    this.uuid = uuid;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(final Date date) {
    this.date = date;
  }

  public PostDto getFirstPost() {
    return firstPost;
  }

  public void setFirstPost(final PostDto firstPost) {
    this.firstPost = firstPost;
  }

  public List<PostDto> getLastPosts() {

    if (null == this.lastPosts) {
      this.lastPosts = new ArrayList<PostDto>();
    }

    return lastPosts;
  }

  public void setLastPosts(final List<PostDto> lastPosts) {
    this.lastPosts = lastPosts;
  }

  @Override
  public String toString() {
    return "ThreadDto{" + "uuid=" + uuid + ", date=" + date + ", firstPost=" + firstPost + ", lastPosts=" + lastPosts + '}';
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ThreadDto other = (ThreadDto) obj;
    if ((this.uuid == null) ? (other.uuid != null) : !this.uuid.equals(other.uuid)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
    return hash;
  }
}
