package org.adrianwalker.jboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.adrianwalker.jboard.client.view.BoardView;

public final class Board implements EntryPoint {

  @Override
  public void onModuleLoad() {
    String pageParameter = Window.Location.getParameter("page");
    if (null == pageParameter || pageParameter.isEmpty()) {
      pageParameter = "1";
    }
    int page = Integer.valueOf(pageParameter);

    RootPanel rootPanel = RootPanel.get();
    rootPanel.add(new BoardView(page));
  }
}
