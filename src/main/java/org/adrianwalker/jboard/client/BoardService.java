package org.adrianwalker.jboard.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.adrianwalker.jboard.common.dto.BoardPageDto;

@RemoteServiceRelativePath("board")
public interface BoardService extends RemoteService {

  BoardPageDto readPage(int page);
}
