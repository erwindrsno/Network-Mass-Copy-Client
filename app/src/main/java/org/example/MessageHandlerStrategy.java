
package org.example;

import java.nio.ByteBuffer;

public interface MessageHandlerStrategy {
  void handleString(String message);

  void handleByte(ByteBuffer buffer);
}
