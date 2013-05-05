package org.adrianwalker.jboard.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.adrianwalker.jboard.client.BoardConstants;
import org.adrianwalker.jboard.client.BoardService;
import org.adrianwalker.jboard.client.BoardServiceAsync;
import org.adrianwalker.jboard.common.dto.BoardPageDto;
import org.adrianwalker.jboard.common.dto.ThreadDto;

public final class BoardView extends Composite {

  interface BoardViewUiBinder extends UiBinder<Widget, BoardView> {
  }
  private static BoardServiceAsync service = GWT.create(BoardService.class);
  private static BoardViewUiBinder uiBinder = GWT.create(BoardViewUiBinder.class);
  private static BoardConstants constants = GWT.create(BoardConstants.class);
  @UiField
  Label title;
  @UiField
  Label description;
  @UiField
  SubmitThread submit;
  @UiField
  Panel threadsPanel;
  @UiField
  Panel pagingPanel;

  public BoardView(final int page) {

    Window.setTitle(constants.title() + " - " + constants.description());

    initWidget(uiBinder.createAndBindUi(this));

    submit.setView(this);
    title.setText(constants.title());
    description.setText(constants.description());

    update(page);
  }

  public void update(final int page) {

    service.readPage(page, new AsyncCallback<BoardPageDto>() {

      @Override
      public void onFailure(Throwable caught) {
        Window.alert(caught.getMessage());
      }

      @Override
      public void onSuccess(BoardPageDto result) {
        threadsPanel.clear();

        for (ThreadDto threadDto : result.getThreads()) {
          threadsPanel.add(new Thread(threadDto));
        }

        pagingPanel.clear();

        boolean first = true;

        for (int pageNumber = 1; pageNumber <= result.getPages(); pageNumber++) {

          Widget pageWidget;
          if (page == pageNumber) {
            pageWidget = new Label(String.valueOf(pageNumber));
          } else {
            pageWidget = new Anchor(String.valueOf(pageNumber), "board.html?page=" + pageNumber);
          }

          if (first) {
            first = false;
          } else {
            pagingPanel.add(new HTML("&nbsp;"));
          }

          pagingPanel.add(pageWidget);
        }
      }
    });
  }
}
