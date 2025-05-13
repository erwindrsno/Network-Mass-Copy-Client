package org.websocket_client.util;

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
    // Path path = Path.of("D:\\Ujian\\");
    // for (int i = 0; i < fcm.getMapOfChunks().size(); i++) {
    //
    // }
    // for (int i = 0; i < this.listOfChunkMaps.get(this.receiveFileCounter).size();
    // i++) {
    // this.listFos.get(this.receiveFileCounter).write(this.listOfChunkMaps.get(this.receiveFileCounter).get(i));
    // }
  }
}
