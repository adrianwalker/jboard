package org.adrianwalker.jboard.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.adrianwalker.jboard.client.BoardConstants;
import org.adrianwalker.jboard.common.dto.PostDto;
import org.adrianwalker.jboard.common.dto.ThreadDto;

public final class Thread extends Composite {

  interface ThreadUiBinder extends UiBinder<Widget, Thread> {
  }
  private static ThreadUiBinder uiBinder = GWT.create(ThreadUiBinder.class);
  private static BoardConstants constants = GWT.create(BoardConstants.class);
  @UiField
  Label date;
  @UiField
  Label subject;
  @UiField
  Label uuid;
  @UiField
  SimplePanel imagePanel;
  @UiField
  HTML comment;
  @UiField
  Anchor reply;
  @UiField
  Panel postPanel;

  public Thread(final ThreadDto thread) {

    initWidget(uiBinder.createAndBindUi(this));

    PostDto firstPost = thread.getFirstPost();

    String image = firstPost.getImage();
    String thumb = firstPost.getThumb();
    if (!image.isEmpty() && !thumb.isEmpty()) {
      String thumbUrl = constants.images() + "/" + thread.getUuid() + "/" + firstPost.getUuid() + "/" + thumb;
      String imageUrl = constants.images() + "/" + thread.getUuid() + "/" + firstPost.getUuid() + "/" + image;
      imagePanel.add(new ImageAnchor(new Image(thumbUrl), imageUrl));
    }

    uuid.setText(firstPost.getUuid());
    date.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(thread.getDate()));
    subject.setText(firstPost.getSubject());
    comment.setHTML(new SafeHtmlBuilder().appendEscapedLines(firstPost.getComment()).toSafeHtml());
    reply.setHref("thread.html?uuid=" + thread.getUuid());

    for (PostDto postDto : thread.getLastPosts()) {
      postPanel.add(new Post(thread.getUuid(), postDto));
    }

    uuid.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        Window.Location.assign("thread.html?uuid=" + thread.getUuid() + "#" + uuid.getText());
      }
    });
  }
}
