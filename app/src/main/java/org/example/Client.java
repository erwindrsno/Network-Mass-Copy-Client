package org.example;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.hash.Hashing;
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

  private List<FileMetadata> listFileMetadata;
  private List<Map<Integer, byte[]>> listOfChunkMaps;
  private String title;
  private Integer entryId;
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
    } else if (message.startsWith("DEL|")) {
      String toDeleteFileName = message.substring(message.indexOf('|') + 1);
      Path toDeletePath = Paths.get(toDeleteFileName);

      try {
        boolean isDeleted = Files.deleteIfExists(toDeletePath);
        if (isDeleted) {
          logger.info("File is deleted.");
        } else {
          logger.info("File is not EXIST");
        }
      } catch (Exception e) {
        logger.error(e.getMessage());
      }
    } else if (message.startsWith("server/")) {
      this.serverHandler.handleString(message.substring(7));
    }
  }

  @Override
  public void onMessage(ByteBuffer buffer) {
    this.serverHandler.handleByte(buffer);
    // if (!this.isFinished) {
    // try {
    // byte[] data = buffer.array();
    // this.listOfChunkMaps.get(this.receiveFileCounter).put(this.receiveChunkCounter,
    // data);
    // this.receiveChunkCounter++;
    // if (this.listOfChunkMaps.get(this.receiveFileCounter).size() ==
    // this.listFileMetadata
    // .get(this.receiveFileCounter)
    // .getChunkCount()) {
    // logger.info("File transfer successfully!");
    //
    // for (int i = 0; i < this.listOfChunkMaps.get(this.receiveFileCounter).size();
    // i++) {
    // this.listFos.get(this.receiveFileCounter).write(this.listOfChunkMaps.get(this.receiveFileCounter).get(i));
    // }
    // this.listFos.get(receiveFileCounter).close();
    //
    // String hashedClient = Hashing.sha256()
    // .hashBytes(Files.readAllBytes(this.listPath.get(this.receiveFileCounter)))
    // .toString();
    //
    // if
    // (hashedClient.equals(this.listFileMetadata.get(this.receiveFileCounter).getSignature()))
    // {
    // logger.info("FILE VERIFIED");
    // } else {
    // this.listPath.get(this.receiveFileCounter).toFile().delete();
    // logger.info("FILE CORRUPTED");
    // }
    // Path newname = Paths
    // .get("files/" + this.title + "/" +
    // this.listFileMetadata.get(this.receiveFileCounter).getFileName());
    // logger.info(this.listFileMetadata.get(this.receiveFileCounter).getFileName());
    // Files.move(this.listPath.get(this.receiveFileCounter), newname,
    // StandardCopyOption.REPLACE_EXISTING);
    //
    // this.receiveChunkCounter = 0;
    // this.receiveFileCounter++;
    // if (this.receiveFileCounter < this.listOfChunkMaps.size()) {
    // send("file~" + this.receiveFileCounter + "CHUNK-ID~" +
    // this.receiveChunkCounter);
    // } else if (this.receiveFileCounter == this.listOfChunkMaps.size() - 1) {
    // logger.info("ALL FILES RECEVIED.");
    // this.isFinished = true;
    // // this.clearAllListAndMap();
    // }
    // } else {
    // if (this.receiveFileCounter < this.listOfChunkMaps.size()) {
    // send("file~" + this.receiveFileCounter + "CHUNK-ID~" +
    // this.receiveChunkCounter);
    // logger.info("Receiving...");
    // }
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    System.out.println("closed with exit code " + code + " additional info: " + reason);
  }

  @Override
  public void onError(Exception ex) {
    System.err.println("an error occurred:" + ex);
  }

  public void validateFileAndHandleAcl() {
    try {
      Path filePath = Paths.get(this.fileMetadata.getFileName());
      // String user = this.fileMetadata.getUser();
      String signature = this.fileMetadata.getSignature();

      Set<AclEntryPermission> permissions = this.fileMetadata.getAclEntry();

      String hashedClient = Hashing.sha256().hashBytes(Files.readAllBytes(toBeReceived)).toString();

      if (hashedClient.equals(signature)) {
        logger.info("FILE VERIFIED");
      } else {
        toBeReceived.toFile().delete();
        logger.info("FILE CORRUPTED");
        return;
      }
      // file rename
      // sourcePath is created beforehand
      Files.move(toBeReceived, filePath, StandardCopyOption.REPLACE_EXISTING);

      // UserPrincipal userPrincipal =
      // filePath.getFileSystem().getUserPrincipalLookupService()
      // .lookupPrincipalByName(user);

      // AclFileAttributeView aclView = Files.getFileAttributeView(filePath,
      // AclFileAttributeView.class);

      // AclEntry aclEntry = AclEntry.newBuilder()
      // .setType(AclEntryType.ALLOW)
      // .setPrincipal(userPrincipal)
      // .setFlags(AclEntryFlag.DIRECTORY_INHERIT, AclEntryFlag.FILE_INHERIT)
      // .setPermissions(permissions)
      // .build();

      // List<AclEntry> acl = aclView.getAcl();
      // acl.add(0, aclEntry);
      // aclView.setAcl(acl);
      logger.info("File received safely, phew");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void clearAllListAndMap() {
    for (Map<Integer, byte[]> map : this.listOfChunkMaps) {
      map.clear();
    }
    this.listOfChunkMaps.clear();
    this.listFileMetadata.clear();
    this.listPath.clear();
    this.listFos.clear();
    this.receiveFileCounter = 0;
    this.receiveChunkCounter = 0;
    this.entryId = null;
    this.title = "";
  }
}

// Catatan Utama
// - Mekanisme monitoring client sudah ada, dengan cara pinging. Tapi coba cara
// lain.
// - Protocol SEND_FILE sudah ada, dan sudah coba implementasi.

// Catanan Tugas
// - Coba monitoring menggunakan onOpen dan onClose.
