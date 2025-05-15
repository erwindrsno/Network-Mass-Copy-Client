package org.websocket_client.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.Client;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;

import com.google.inject.Inject;

public class FileWriter {
  private Logger logger;
  private Client client;

  @Inject
  public FileWriter(Client client) {
    this.logger = LoggerFactory.getLogger(Client.class);
    this.client = client;
  }

  public void writeFile(FileChunkMetadata fcm, FileAccessInfo fai) {
    try {
      Path path = Path.of(fai.getPath());
      Files.createDirectories(path.getParent());

      Files.deleteIfExists(path);

      this.logger.info("the chunk count is: " + fcm.getChunkCount());
      for (long i = 0; i < fcm.getChunkCount(); i++) {
        if(i == fcm.getChunkCount() - 1){
          this.logger.info("The file successfully written!");
        }
        Files.write(path, fcm.getMapOfChunks().get(i), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      }
      this.client.send("client/" + "ok/" + fai.getId());
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }
}