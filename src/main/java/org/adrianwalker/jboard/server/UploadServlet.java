package org.adrianwalker.jboard.server;

import java.util.List;
import org.apache.commons.fileupload.FileItem;
import java.io.BufferedWriter;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServlet;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.adrianwalker.jboard.server.Constants.POST_FILENAME;
import static org.adrianwalker.jboard.server.Constants.THUMB_FILENAME;

public final class UploadServlet extends HttpServlet {

  private static final Pattern UUID_PATTERN = Pattern.compile("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}");
  private static final String COMMENT_FORM_FIELD = "comment";
  private static final long COMMENT_LENGTH = 10 * 1024;
  private static final long BYTES_IN_MB = 1 * 1024 * 1024;
  private static final long IMAGE_SIZE = 10;
  private static final String PROPERTIES_FILE = "WEB-INF/classes/BoardConfiguration.properties";
  private static final Logger LOGGER = LoggerFactory.getLogger(UploadServlet.class);
  private static final String SUBJECT_FORM_FIELD = "subject";
  private static final String UUID_FORM_FIELD = "uuid";
  private static final String BUMP_FORM_FIELD = "bump";
  private static final int THREAD_THUMB_SIZE = 250;
  private static final int POST_THUMB_SIZE = 125;
  private String uploads;
  private String convert;
  private int maxThreads;
  private int maxPosts;

  @Override
  public void init() throws ServletException {
    Properties properties = new Properties();
    try {
      properties.load(getServletContext().getResourceAsStream(PROPERTIES_FILE));
    } catch (IOException ioe) {
      throw new ServletException(ioe);
    }

    uploads = properties.getProperty("uploads");
    convert = properties.getProperty("convert");
    maxThreads = Integer.valueOf(properties.getProperty("maxThreads"));
    maxPosts = Integer.valueOf(properties.getProperty("maxPosts"));
  }

  @Override
  protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
    try {
      response.setContentType("text/html");
      response.getWriter().print(uploadFile(request));
    } catch (FileUploadException fue) {
      throw new ServletException(fue);
    } catch (InterruptedException ie) {
      throw new ServletException(ie);
    }
  }

  private String uploadFile(final HttpServletRequest request) throws FileUploadException, IOException, InterruptedException {

    if (!ServletFileUpload.isMultipartContent(request)) {
      return "<p class='Error'>Error request is not multipart</p>";
    }
    FileItemFactory fileItemFactory = new DiskFileItemFactory();
    ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
    List<FileItem> items = servletFileUpload.parseRequest(request);

    String subject = "";
    String comment = "";
    String image = "";
    String uuid = "";
    String bump = "";
    FileItem fileItem = null;

    for (final FileItem item : items) {
      if (item.isFormField()) {
        String fieldName = item.getFieldName();
        String fieldValue = item.getString();

        if (fieldName.equals(SUBJECT_FORM_FIELD)) {
          subject = fieldValue.trim();
        } else if (fieldName.equals(COMMENT_FORM_FIELD)) {
          comment = fieldValue.trim();
        } else if (fieldName.equals(UUID_FORM_FIELD)) {
          uuid = fieldValue.trim();
        } else if (fieldName.equals(BUMP_FORM_FIELD)) {
          bump = fieldValue.trim();
        }

      } else {
        fileItem = item;
        image = fileItem.getName();
      }
    }

    if (fileItem == null) {
      return "<p class='Error'>No file</p>";
    }

    if (!image.isEmpty() && !fileItem.getContentType().contains("image")) {
      return "<p class='Error'>Invalid image type</p>";
    }

    if (comment.isEmpty()) {
      return "<p class='Error'>Enter a comment</p>";
    }

    if (comment.length() > COMMENT_LENGTH) {
      return String.format("<p class='Error'>Comment is to long, maximum %s characters</p>", COMMENT_LENGTH);
    }

    if (!uuid.isEmpty() && !UUID_PATTERN.matcher(uuid).matches()) {
      return "<p class='Error'>Invalid thread</p>";
    }

    File boardDir = new File(uploads);
    File threadDir = new File(boardDir, uuid);
    long lastModified = System.currentTimeMillis();
    boolean threadExists = threadDir.exists() && !uuid.isEmpty();

    if (!threadExists && image.isEmpty()) {
      return "<p class='Error'>Select an image</p>";
    }

    if (threadExists) {
      lastModified = threadDir.lastModified();
      int posts = CommonUtil.readDirs(threadDir).length;
      if (posts >= maxPosts) {
        return "<p class='Error'>Maximum number of posts reached</p>";
      }
    } else {
      uuid = UUID.randomUUID().toString();
      threadDir = new File(boardDir, uuid);
      threadDir.mkdirs();
    }

    String post = UUID.randomUUID().toString();
    File postDir = new File(threadDir, post);
    boolean postExists = postDir.exists();
    if (postExists) {
      return "<p class='Error'>Post already exists</p>";
    } else {
      postDir.mkdirs();
    }

    if (!image.isEmpty()) {

      if (fileItem.getSize() <= 0) {
        if (threadExists) {
          CommonUtil.deleteDir(postDir);
        } else {
          CommonUtil.deleteDir(threadDir);
        }
        return String.format("<p class='Error'>Empty image file</p>", IMAGE_SIZE);
      }

      if (fileItem.getSize() > IMAGE_SIZE * BYTES_IN_MB) {
        if (threadExists) {
          CommonUtil.deleteDir(postDir);
        } else {
          CommonUtil.deleteDir(threadDir);
        }
        return String.format("<p class='Error'>Image too large, max size %sMB</p>", IMAGE_SIZE);
      }

      File imageFile = new File(postDir, image);
      Streams.copy(fileItem.getInputStream(), new FileOutputStream(imageFile), true);

      File thumbFile = new File(postDir, THUMB_FILENAME);
      int thumbSize = threadExists ? POST_THUMB_SIZE : THREAD_THUMB_SIZE;
      boolean thumbCreated = convert(imageFile, thumbFile, thumbSize);
      if (!thumbCreated) {
        if (threadExists) {
          CommonUtil.deleteDir(postDir);
        } else {
          CommonUtil.deleteDir(threadDir);
        }
        return "<p class='Error'>Error uploading image</p>";
      }

      LOGGER.info(String.format("uploaded file %s", image));
    }

    File file = new File(postDir, POST_FILENAME);
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    writer.write(image);
    writer.newLine();
    writer.write(subject);
    writer.newLine();
    writer.write(comment);
    writer.close();

    if (bump.isEmpty()) {
      threadDir.setLastModified(lastModified);
    }

    CommonUtil.prune(boardDir, maxThreads, false);

    if (threadExists) {
      return "<p class='Success'>Post submitted</p>";
    } else {
      return "<p class='Success'>Thread submitted</p>";
    }
  }

  private boolean convert(final File in, final File out, final int size) throws InterruptedException, IOException {
    String[] command = {
      convert,
      "-geometry",
      size + "x" + size,
      "-quality",
      "75",
      in.getAbsolutePath(),
      out.getAbsolutePath()
    };

    return CommonUtil.exec(command) == 0;
  }
}
