package org.websocket_client;

import java.nio.ByteBuffer;

public interface MessageHandlerStrategy {
  void handleString(String message);

  void handleByte(ByteBuffer buffer);
}
