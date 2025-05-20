package org.websocket_client.handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.Client;
import org.websocket_client.WebSocketModule;
import org.websocket_client.model.Context;
import org.websocket_client.model.DirectoryAccessInfo;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;
import org.websocket_client.util.AclHandler;
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
  private List<DirectoryAccessInfo> listDai;
  private int fileCounter;
  private long chunkCounter;
  private boolean readyToReceive;
  private FileVerifier fileVerifier;
  private Client client;
  private FileWriter fileWriter;
  private AclHandler aclHandler;

  @Inject
  public ServerHandler(FileVerifier fileVerifier, Client client, FileWriter fileWriter, AclHandler aclHandler) {
    this.logger = LoggerFactory.getLogger(WebSocketModule.class);
    this.context = null;
    this.fileVerifier = fileVerifier;
    this.listFcm = null;
    this.listFai = null;
    this.listDai = null;
    this.client = client;
    this.fileWriter = fileWriter;
    this.aclHandler = aclHandler;
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

          Optional<FileAccessInfo> retrievedFai = listFai.stream()
              .filter(fai -> fai.getFilename().equals(tempFcm.getFilename()))
              .findFirst();

          boolean isWritten = this.fileWriter.writeFile(tempFcm, retrievedFai.get());
          if (isWritten) {
            this.client.send("client/" + "ok/copy/" + retrievedFai.get().getId());
          }
        } else {
          logger.error("a file is NOT SAFE..., go next!");
        }

        this.fileVerifier.clear();

        this.chunkCounter = 0;

        if (this.fileCounter == this.listFcm.size() - 1) {
          this.fileCounter = 0;
          this.readyToReceive = false;
          logger.info("All files received. HOORAYYYYYYY");
          try {
            DirectoryAccessInfo tempDai = this.listDai.get(0);
            this.client.send("client/fin/copy/" + tempDai.getId());
          } catch (Exception e) {
            logger.error(e.getMessage(), e);
          }

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
    if (message.startsWith("metadata/copy/")) {
      String json = message.substring(14);
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);

      try {
        this.context = mapper.readValue(json, Context.class);
        this.listFcm = this.context.getListFcm();
        this.listFai = this.context.getListFai();
        this.listDai = this.context.getListDai();

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
    } else if (message.startsWith("metadata/takeown/")) {
      String json = message.substring(17);
      try {
        ObjectMapper mapper = new ObjectMapper();
        this.context = mapper.readValue(json, Context.class);
        this.listFai = this.context.getListFai();
        this.listDai = this.context.getListDai();
        // Enable pretty-printing
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        logger.info(json);

        List<FileAccessInfo> tempListFai = this.context.getListFai();

        Path path = Path.of(this.listDai.get(0).getPath());

        boolean isTakeowned = this.aclHandler.handleTakeownAcl(path, this.listDai.get(0).getOwner());

        if (isTakeowned) {
          this.client.send("client/fin/takeown/" + this.listDai.get(0).getId());
        } else {
          throw new Exception("takeowned failed.");
        }
      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    } else if (message.startsWith("metadata/delete/")) {
      String json = message.substring(16);
      try {
        ObjectMapper mapper = new ObjectMapper();
        this.context = mapper.readValue(json, Context.class);
        this.listFai = this.context.getListFai();
        this.listDai = this.context.getListDai();
        // Enable pretty-printing
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        Path path = Path.of(this.listDai.get(0).getPath());

        try {

            logger.info("deleting...");
          boolean isDeleted = deleteDirectoryRecursively(path);
          if (isDeleted) {
            this.client.send("client/fin/delete/" + this.listDai.get(0).getId());
          }
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
        }

      } catch (Exception e) {
        logger.error(e.getMessage(), e);
      }
    }
  }

  public boolean deleteDirectoryRecursively(Path path) {
    if (!Files.exists(path))
      return true; // Nothing to delete is also "successful"

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
      return true; // If we got here, all deletions succeeded
    } catch (IOException e) {
      logger.error("Failed to delete directory: " + path, e);
      return false;
    }
  }

}
