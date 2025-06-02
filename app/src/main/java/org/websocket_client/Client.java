package org.websocket_client;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.*;
import org.websocket_client.handler.ServerHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class Client extends WebSocketClient {
  Path toBeReceived = Paths.get("toBeReceived");
  FileOutputStream fos;
  Logger logger = LoggerFactory.getLogger(Client.class);

  boolean readyToReceiveFile = false;

  byte[] fileBytes; // complete file in bytes
  int currIdx = 0;
  long fileSize;
  long chunkSize;
  long chunkCount;

  Map<Integer, byte[]> bytesMap = new HashMap<>();
  int bytesIdx = 0;

  FileMetadata fileMetadata;

  List<Path> listPath = new ArrayList<>();
  List<FileOutputStream> listFos = new ArrayList<>();
  int receiveFileCounter = 0;
  int receiveChunkCounter = 0;
  boolean isFinished;

  private ServerHandler serverHandler;

  public Client(URI serverUri, Draft draft) {
    super(serverUri, draft);
  }

  public Client(URI serverURI) {
    super(serverURI);
  }

  @Inject
  public void injectDependencies(ServerHandler serverHandler) {
    this.serverHandler = serverHandler;
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    logger.info("Connected to server : " + handshakedata.getHttpStatusMessage());
  }

  @Override
  public void onMessage(String message) {
    if (message.startsWith("FILE-METADATA~")) {
      try {
        fos = new FileOutputStream(toBeReceived.toFile());
      } catch (Exception e) {
        e.printStackTrace();
      }
      String json = message.substring(message.indexOf('~') + 1);
      ObjectMapper mapper = new ObjectMapper();
      try {
        this.fileMetadata = mapper.readValue(json, FileMetadata.class);
        this.fileSize = this.fileMetadata.getFileSize();
        this.chunkSize = this.fileMetadata.getChunkSize();
        this.chunkCount = this.fileMetadata.getChunkCount();
        // this.fileBytes = new byte[(int) this.fileSize];
        this.readyToReceiveFile = true;
        logger.info("RECEIVED META DATA");
        logger.info("File size is : " + this.fileSize);
        logger.info("chunk size is : " + this.chunkSize);
        logger.info("chunk count is : " + this.chunkCount);
        // logger.info("file bytes length is : " + this.fileBytes.length);
        for (long i = 0; i < chunkCount; i++) {
          send("CHUNK-ID~" + i);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else if (message.startsWith("server/")) {
      this.serverHandler.handleString(message.substring(7));
    }
  }

  @Override
  public void onMessage(ByteBuffer buffer) {
    this.serverHandler.handleByte(buffer);
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    System.out.println("closed with exit code " + code + " additional info: " + reason);
  }

  @Override
  public void onError(Exception ex) {
    System.err.println("an error occurred:" + ex);
  }
}
