package org.websocket_client;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.handler.ServerHandler;
import org.websocket_client.util.FileVerifier;
import org.websocket_client.util.FileWriter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class WebSocketModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("host")).toInstance("10.100.70.211");
    bind(Integer.class).annotatedWith(Names.named("port")).toInstance(8887);
    bind(ServerHandler.class).in(Singleton.class);
    bind(FileVerifier.class).in(Singleton.class);
    bind(FileWriter.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  public Logger provideLogger() {
    Logger logger = LoggerFactory.getLogger(WebSocketModule.class);
    return logger;
  }

  @Provides
  @Singleton
  public Client provideClient(Logger logger, @Named("host") String host,
      @Named("port") int port) {
    try {
      URI uri = new URI("ws://192.168.0.108:8887");
      return new Client(uri);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }
}
