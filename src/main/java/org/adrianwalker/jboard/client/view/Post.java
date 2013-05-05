package org.adrianwalker.jboard.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.adrianwalker.jboard.client.BoardConstants;
import org.adrianwalker.jboard.common.dto.PostDto;

public final class Post extends Composite {

  interface PostUiBinder extends UiBinder<Widget, Post> {
  }
  private static PostUiBinder uiBinder = GWT.create(PostUiBinder.class);
  private static BoardConstants constants = GWT.create(BoardConstants.class);
  @UiField
  Anchor anchor;
  @UiField
  Label date;
  @UiField
  Label uuid;
  @UiField
  Label subject;
  @UiField
  SimplePanel imagePanel;
  @UiField
  HTML comment;

  public Post(final String threadUuid, final PostDto postDto) {
    initWidget(uiBinder.createAndBindUi(this));

    anchor.setName(postDto.getUuid());
    String image = postDto.getImage();
    String thumb = postDto.getThumb();
    if (!image.isEmpty() && !thumb.isEmpty()) {

      String thumbUrl = constants.images() + "/" + threadUuid + "/" + postDto.getUuid() + "/" + thumb;
      String imageUrl = constants.images() + "/" + threadUuid + "/" + postDto.getUuid() + "/" + image;
      imagePanel.add(new ImageAnchor(new Image(thumbUrl), imageUrl));
    }

    uuid.setText(postDto.getUuid());
    date.setText(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(postDto.getDate()));
    subject.setText(postDto.getSubject());
    comment.setHTML(toHtml(postDto.getComment()));
  }

  public Post(final String threadUuid, final PostDto postDto, final SubmitPost submit) {


    this(threadUuid, postDto);

    uuid.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        submit.comment.setText(submit.comment.getText() + ">>" + postDto.getUuid() + "\n");
      }
    });
  }

  private String toHtml(final String comment) {
    String html = new SafeHtmlBuilder().appendEscapedLines(comment).toSafeHtml().asString();
    html = html.replaceAll("&gt;&gt;(\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12})", "<a href='#$1'>&gt;&gt;$1</a>");
    return html;
  }
}
