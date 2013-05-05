package org.adrianwalker.jboard.server;

import org.adrianwalker.jboard.common.dto.ThreadPageDto;
import org.adrianwalker.jboard.common.dto.BoardPageDto;
import org.adrianwalker.jboard.client.ThreadService;
import org.adrianwalker.jboard.common.dto.ThreadDto;
import java.io.IOException;
import javax.servlet.ServletException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.adrianwalker.jboard.client.BoardService;
import org.adrianwalker.jboard.common.dto.PostDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.adrianwalker.jboard.server.Constants.POST_FILENAME;
import static org.adrianwalker.jboard.server.Constants.THUMB_FILENAME;

public final class ServiceServlet extends RemoteServiceServlet implements BoardService, ThreadService {

  private static final String PROPERTIES_FILE = "WEB-INF/classes/BoardConfiguration.properties";
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceServlet.class);
  private String uploads;
  private int pageSize;
  private int lastPosts;

  @Override
  public void init() throws ServletException {
    Properties properties = new Properties();
    try {
      properties.load(getServletContext().getResourceAsStream(PROPERTIES_FILE));
    } catch (IOException ioe) {
      throw new ServletException(ioe);
    }

    uploads = properties.getProperty("uploads");
    lastPosts = Integer.valueOf(properties.getProperty("lastPosts"));
    pageSize = Integer.valueOf(properties.getProperty("pageSize"));
  }

  @Override
  public BoardPageDto readPage(int page) {

    if (page < 1) {
      page = 1;
    }

    File dir = new File(this.uploads);

    File[] threads = CommonUtil.readDirs(dir);
    CommonUtil.sortDirs(threads, true);

    int firstThread = pageSize * (page - 1);
    int lastThread = firstThread + this.pageSize;
    int threadCount = threads.length;
    if (threadCount < lastThread) {
      lastThread = threadCount;
    }

    int pages = (int) Math.ceil((double) threadCount / (double) pageSize);

    if (page > pages) {
      page = pages;
    }    
    
    List<ThreadDto> threadDtos;
    if (firstThread < threadCount) {
      threadDtos = new ArrayList<ThreadDto>();

      for (int i = firstThread; i < lastThread; i++) {

        File thread = threads[i];

        ThreadDto threadDto = new ThreadDto();
        threadDto.setUuid(thread.getName());
        threadDto.setDate(new Date(thread.lastModified()));

        File threadDir = new File(dir, thread.getName());
        File[] posts = CommonUtil.readDirs(threadDir);
        CommonUtil.sortDirs(posts, false);

        File post = posts[0];
        File postDir = new File(threadDir, post.getName());
        File file = new File(postDir, POST_FILENAME);
        PostDto postDto = readPostFile(file, post);
        threadDto.setFirstPost(postDto);

        int replies = posts.length - 1;
        int lastPosts = replies < this.lastPosts ? replies : this.lastPosts;

        for (int j = 1; j <= lastPosts; j++) {
          post = posts[replies - (lastPosts - j)];
          postDir = new File(threadDir, post.getName());
          file = new File(postDir, POST_FILENAME);
          postDto = readPostFile(file, post);
          threadDto.getLastPosts().add(postDto);
        }

        threadDtos.add(threadDto);
      }


    } else {
      threadDtos = Collections.EMPTY_LIST;
    }

    BoardPageDto boardPageDto = new BoardPageDto();
    boardPageDto.setThreads(threadDtos);
    boardPageDto.setPage(page);
    boardPageDto.setPages(pages);

    LOGGER.info(String.format("returning %s", boardPageDto));

    return boardPageDto;
  }

  @Override
  public ThreadPageDto readPage(final String uuid) {

    File threadDir = new File(this.uploads, uuid);

    File[] posts = CommonUtil.readDirs(threadDir);
    CommonUtil.sortDirs(posts, false);

    List<PostDto> postDtos = new ArrayList<PostDto>();
    for (File post : posts) {
      File postDir = new File(threadDir, post.getName());
      File file = new File(postDir, POST_FILENAME);
      PostDto postDto = readPostFile(file, post);
      postDtos.add(postDto);
    }

    ThreadPageDto threadPageDto = new ThreadPageDto();
    threadPageDto.setPosts(postDtos);

    LOGGER.info(String.format("returning %s", threadPageDto));

    return threadPageDto;
  }

  private PostDto readPostFile(final File file, final File post) {
    String image = "";
    String subject = "";
    String comment = "";
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      image = reader.readLine();
      subject = reader.readLine();
      StringBuilder builder = new StringBuilder();
      int c;
      while ((c = reader.read()) != -1) {
        builder.append((char) c);
      }
      comment = builder.toString();
    } catch (Throwable t) {
      LOGGER.error("error read posts", t);
    } finally {
      try {
        if (null != reader) {
          reader.close();
        }
      } catch (IOException ioe) {
        LOGGER.error("error closing file reader", ioe);
      }

    }
    PostDto postDto = createPostDto(post, subject, comment, image);
    return postDto;
  }

  private PostDto createPostDto(final File post, final String subject, final String comment, final String image) {
    PostDto postDto = new PostDto();
    postDto.setUuid(post.getName());
    postDto.setDate(new Date(post.lastModified()));
    postDto.setSubject(subject);
    postDto.setComment(comment);
    postDto.setThumb(THUMB_FILENAME);
    postDto.setImage(image);
    return postDto;
  }
}
