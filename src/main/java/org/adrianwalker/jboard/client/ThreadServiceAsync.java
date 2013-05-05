package org.adrianwalker.jboard.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.adrianwalker.jboard.common.dto.ThreadPageDto;

public interface ThreadServiceAsync {

  void readPage(String uuid, AsyncCallback<ThreadPageDto> asyncCallback);
}
