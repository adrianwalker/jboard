package org.adrianwalker.jboard.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import org.adrianwalker.jboard.client.view.ThreadView;

public final class Thread implements EntryPoint {

  @Override
  public void onModuleLoad() {

    String uuid = Window.Location.getParameter("uuid");

    RootPanel rootPanel = RootPanel.get();
    rootPanel.add(new ThreadView(uuid));
  }
}
