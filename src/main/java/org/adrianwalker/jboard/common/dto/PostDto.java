package org.adrianwalker.jboard.common.dto;

import java.io.Serializable;
import java.util.Date;

public final class PostDto implements Serializable {

  private String uuid;
  private Date date;
  private String subject;
  private String comment;
  private String image;
  private String thumb;

  public PostDto() {
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

  public String getComment() {
    return comment;
  }

  public void setComment(final String comment) {
    this.comment = comment;
  }

  public String getImage() {
    return image;
  }

  public void setImage(final String image) {
    this.image = image;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(final String subject) {
    this.subject = subject;
  }

  public String getThumb() {
    return thumb;
  }

  public void setThumb(final String thumb) {
    this.thumb = thumb;
  }
}
