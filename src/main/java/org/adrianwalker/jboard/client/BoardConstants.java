package org.adrianwalker.jboard.client;

import com.google.gwt.i18n.client.Constants;
import com.google.gwt.i18n.client.Constants.DefaultStringValue;

public interface BoardConstants extends Constants {

  @DefaultStringValue("title")
  String title();

  @DefaultStringValue("description")
  String description();

  @DefaultStringValue("images")
  String images();
}