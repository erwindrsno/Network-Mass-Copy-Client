package org.websocket_client.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRemover {
  private final Logger logger;

  public FileRemover() {
    this.logger = LoggerFactory.getLogger(FileRemover.class);
  }

  public boolean deleteDirectory(Path path) {
    if (!Files.exists(path))
      return true;

    try {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
      return true;
    } catch (IOException e) {
      this.logger.error("Failed to delete directory: " + path, e);
      return false;
    }
  }

  public boolean deleteFile(Path path) {
    if (!Files.exists(path)) {
      return true;
    }
    try {
      Files.delete(path);
      return true;
    } catch (Exception e) {
      this.logger.error("failed to delete file: " + path, e);
      return false;
    }
  }
}
