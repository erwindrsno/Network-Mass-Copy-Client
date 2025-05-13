package org.websocket_client.util;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.Client;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;

public class FileWriter {
  private Logger logger;

  public FileWriter() {
    this.logger = LoggerFactory.getLogger(Client.class);
  }

  public void writeFile(FileChunkMetadata fcm, FileAccessInfo fai) {
    this.logger.info("fcm filename: " + fcm.getFilename());
    this.logger.info("fai pathname: " + fai.getPath());
    try {
      Path path = Path.of(fai.getPath());
      Files.createDirectories(path.getParent());
      for (long i = 0; i < fcm.getChunkCount(); i++) {
        Files.write(path, fcm.getMapOfChunks().get(i));
      }
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }

    // for (int i = 0; i < fcm.getMapOfChunks().size(); i++) {
    //
    // }
    // for (int i = 0; i < this.listOfChunkMaps.get(this.receiveFileCounter).size();
    // i++) {
    // this.listFos.get(this.receiveFileCounter).write(this.listOfChunkMaps.get(this.receiveFileCounter).get(i));
    // }
  }
}
