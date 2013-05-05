package org.adrianwalker.jboard.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import org.adrianwalker.jboard.common.dto.ThreadPageDto;

@RemoteServiceRelativePath("thread")
public interface ThreadService extends RemoteService {

  ThreadPageDto readPage(String uuid);
}
