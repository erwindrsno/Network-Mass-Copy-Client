package org.websocket_client.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;

import com.google.inject.Inject;

public class FileWriter {
  private Logger logger;
  private final AclHandler aclHandler;

  @Inject
  public FileWriter(AclHandler aclHandler) {
    this.logger = LoggerFactory.getLogger(FileWriter.class);
    this.aclHandler = aclHandler;
  }

  public boolean createDirectoryAndHandleAcl(FileAccessInfo fai) {
    try {
      Path path = Path.of(fai.getPath());
      Path parent = path.getParent();

      // Delete the file if it exists
      Files.deleteIfExists(path);

      if (Files.exists(parent) && Files.isDirectory(parent)) {
        logger.info("Parent is a directory.");
        Files.deleteIfExists(parent);
      } else {
        logger.info("Parent is not a directory or does not exist. Creating it...");
        Files.createDirectories(parent);
        this.aclHandler.handleCopyAcl(parent, fai);
      }
      return true;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return false;
    }
  }

  public boolean writeFile(FileChunkMetadata fcm, FileAccessInfo fai) {
    try {
      Path path = Path.of(fai.getPath());

      // Delete the file if it exists
      Files.deleteIfExists(path);

      for (long i = 0; i < fcm.getChunkCount(); i++) {
        if (i == fcm.getChunkCount() - 1) {
          this.logger.info("The file successfully written!");
        }
        Files.write(path, fcm.getMapOfChunks().get(i), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      }
      return true;
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return false;
    }
  }
}
