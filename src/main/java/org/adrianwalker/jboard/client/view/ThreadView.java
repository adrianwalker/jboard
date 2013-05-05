package org.adrianwalker.jboard.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.adrianwalker.jboard.client.BoardConstants;
import org.adrianwalker.jboard.client.ThreadService;
import org.adrianwalker.jboard.client.ThreadServiceAsync;
import org.adrianwalker.jboard.common.dto.PostDto;
import org.adrianwalker.jboard.common.dto.ThreadPageDto;

public final class ThreadView extends Composite {

  interface ThreadViewUiBinder extends UiBinder<Widget, ThreadView> {
  }
  private static ThreadServiceAsync service = GWT.create(ThreadService.class);
  private static ThreadViewUiBinder uiBinder = GWT.create(ThreadViewUiBinder.class);
  private static BoardConstants constants = GWT.create(BoardConstants.class);
  @UiField
  Label title;
  @UiField
  Label description;
  @UiField
  SubmitPost submit;
  @UiField
  Panel postsPanel;

  public ThreadView(final String uuid) {

    Window.setTitle(constants.title() + " - " + constants.description());

    initWidget(uiBinder.createAndBindUi(this));

    submit.setView(this);
    submit.uuid.setValue(uuid);
    title.setText(constants.title());
    description.setText(constants.description());

    update(uuid);
  }

  public void update(final String uuid) {

    service.readPage(uuid, new AsyncCallback<ThreadPageDto>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage());
      }

      @Override
      public void onSuccess(ThreadPageDto result) {
        postsPanel.clear();

        for (PostDto post : result.getPosts()) {
          postsPanel.add(new Post(uuid, post, submit));
        }
      }
    });
  }
}
