package org.adrianwalker.jboard.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.adrianwalker.jboard.common.dto.BoardPageDto;

public interface BoardServiceAsync {

  void readPage(int page, AsyncCallback<BoardPageDto> asyncCallback);
}
