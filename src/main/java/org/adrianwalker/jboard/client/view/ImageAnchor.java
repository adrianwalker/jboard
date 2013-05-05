package org.adrianwalker.jboard.client.view;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;

public class ImageAnchor extends Anchor {

  public ImageAnchor(final Image image, final String url) {
    super();

    DOM.insertBefore(getElement(), image.getElement(), DOM.getFirstChild(getElement()));

    setHref(url);
  }
}
