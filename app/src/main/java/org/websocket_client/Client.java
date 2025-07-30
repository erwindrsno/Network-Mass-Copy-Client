package org.websocket_client;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.*;
import org.websocket_client.handler.ServerHandler;

public class Client extends WebSocketClient {
  Logger logger = LoggerFactory.getLogger(Client.class);

  private ServerHandler serverHandler;

  public Client(URI serverUri, Draft draft) {
    super(serverUri, draft);
  }

  public Client(URI serverURI, ServerHandler serverHandler) {
    super(serverURI);
    this.serverHandler = serverHandler;
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    logger.info("Connected to server : " + handshakedata.getHttpStatusMessage());
  }

  @Override
  public void onMessage(String message) {
    if (message.startsWith("server/")) {
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
