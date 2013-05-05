package org.adrianwalker.jboard.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public final class SubmitThread extends Composite {

  interface SubmitThreadUiBinder extends UiBinder<Widget, SubmitThread> {
  }
  private static SubmitThreadUiBinder uiBinder = GWT.create(SubmitThreadUiBinder.class);
  @UiField
  FormPanel form;
  @UiField
  FileUpload file;
  @UiField
  Button submit;
  @UiField
  TextBox subject;
  @UiField
  TextArea comment;
  @UiField
  VerticalPanel messages;
  private BoardView view;

  public SubmitThread() {

    initWidget(uiBinder.createAndBindUi(this));

    submit.addClickHandler(new SubmitClickHandler());
    form.addSubmitHandler(new FormSubmitHandler());
    form.addSubmitCompleteHandler(new FormSubmitCompleteHandler());
  }

  public void setView(final BoardView view) {
    this.view = view;
  }

  private class FormSubmitHandler implements SubmitHandler {

    @Override
    public void onSubmit(final SubmitEvent event) {
      submit.setEnabled(false);
    }
  }

  private class FormSubmitCompleteHandler implements SubmitCompleteHandler {

    @Override
    public void onSubmitComplete(final SubmitCompleteEvent event) {
      view.update(1);
      form.reset();
      submit.setEnabled(true);
      
      addMessage(event.getResults());
    }
  }

  private final class SubmitClickHandler implements ClickHandler {

    @Override
    public void onClick(final ClickEvent event) {
      boolean emptyFile = file.getFilename().isEmpty();
      if (emptyFile) {
        addMessage("<p class='Error'>Select an image</p>");
        return;
      }

      boolean commentEmpty = comment.getText().trim().isEmpty();
      if (commentEmpty) {
        addMessage("<p class='Error'>Enter a comment</p>");
        return;
      }

      form.submit();
    }
  }

  private void addMessage(final String html) {
    final HTML message = new HTML(html);
    messages.insert(message, 0);

    new Timer() {

      @Override
      public void run() {
        messages.remove(message);
      }
    }.schedule(3000);
  }
}
