package org.websocket_client;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.handler.ServerHandler;
import org.websocket_client.util.AclHandler;
import org.websocket_client.util.FileRemover;
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
    bind(AclHandler.class).in(Singleton.class);
    bind(FileRemover.class).in(Singleton.class);
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
      @Named("port") int port, ServerHandler serverHandler) {
    try {
      // URI uri = new URI("ws://192.168.0.114:8887");
      URI uri = new URI("ws://10.100.70.211:8887");
      // URI uri = new URI("ws://" + host + ":" + port);
      return new Client(uri, serverHandler);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }
}
