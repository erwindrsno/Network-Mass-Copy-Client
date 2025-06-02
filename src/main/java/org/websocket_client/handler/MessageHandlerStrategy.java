package org.websocket_client.handler;

import java.nio.ByteBuffer;

public interface MessageHandlerStrategy {
  void handleString(String message);

  void handleByte(ByteBuffer buffer);
}
