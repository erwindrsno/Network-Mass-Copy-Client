package org.websocket_client.handler;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.Client;
import org.websocket_client.WebSocketModule;
import org.websocket_client.model.Context;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;
import org.websocket_client.util.FileVerifier;
import org.websocket_client.util.FileWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.inject.Inject;

public class ServerHandler
    implements MessageHandlerStrategy {

  private final Logger logger;
  private Context context;

  private List<FileChunkMetadata> listFcm;
  private List<FileAccessInfo> listFai;
  private int fileCounter;
  private long chunkCounter;
  private boolean readyToReceive;
  private FileVerifier fileVerifier;
  private Client client;
  private FileWriter fileWriter;

  @Inject
  public ServerHandler(FileVerifier fileVerifier, Client client, FileWriter fileWriter) {
    this.logger = LoggerFactory.getLogger(WebSocketModule.class);
    this.context = null;
    this.fileVerifier = fileVerifier;
    this.listFcm = null;
    this.listFai = null;
    this.client = client;
    this.fileWriter = fileWriter;
  }

  @Override
  public void handleByte(ByteBuffer buffer) {
    if (this.readyToReceive) {
      byte[] data = buffer.array();

      FileChunkMetadata tempFcm = this.listFcm.get(this.fileCounter);
      Map<Long, byte[]> tempMapOfChunks = tempFcm.getMapOfChunks();

      tempMapOfChunks.put(this.chunkCounter, data);
      this.fileVerifier.putBytes(data);

      this.chunkCounter++;

      if (tempMapOfChunks.size() == tempFcm.getChunkCount()) {
        boolean isVerified = this.fileVerifier.verifyHashedBytes(tempFcm.getSignature());
        if (isVerified) {
          logger.info("File is safe. dont worry.");
          this.fileWriter.writeFile(tempFcm, this.listFai.get(this.fileCounter));
        } else {
          logger.error("a file is NOT SAFE..., go next!");
        }

        this.fileVerifier.clear();

        this.chunkCounter = 0;

        if (this.fileCounter == this.listFcm.size() - 1) {
          this.fileCounter = 0;
          this.readyToReceive = false;
          logger.info("All files received. HOORAYYYYYYY");

          return;
        } else {
          this.fileCounter++;
          Map<Long, byte[]> mapOfChunks = new HashMap<>();
          this.listFcm.get(this.fileCounter).setMapOfChunks(mapOfChunks);
          this.fileVerifier.prepare();
          this.client
              .send("client/file~" + this.listFcm.get(this.fileCounter).getUuid() + "CHUNK-ID~" + this.chunkCounter);
        }
      } else {
        this.client.send("client/file~" + tempFcm.getUuid() + "CHUNK-ID~" + this.chunkCounter);
      }
    }
  }

  @Override
  public void handleString(String message) {
    if (message.startsWith("metadata/")) {
      String json = message.substring(9);
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT); // pretty print
      logger.info(json);

      try {
        this.context = mapper.readValue(json, Context.class);
        this.listFcm = this.context.getListFcm();
        this.listFai = this.context.getListFai();

        this.fileCounter = 0;
        this.chunkCounter = 0;
        this.readyToReceive = true;

        Map<Long, byte[]> mapOfChunks = new HashMap<>();

        this.listFcm.get(this.fileCounter).setMapOfChunks(mapOfChunks);
        this.fileVerifier.prepare();

        this.client.send("client/file~" + this.listFcm.get(this.fileCounter).getUuid() +
            "CHUNK-ID~" + this.chunkCounter);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}
