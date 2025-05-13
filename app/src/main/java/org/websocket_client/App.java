package org.websocket_client;

import java.net.URISyntaxException;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Hello world!
 */
public class App {
  public static void main(String[] args) throws URISyntaxException {
    Injector injector = Guice.createInjector(new WebSocketModule());
    Client client = injector.getInstance(Client.class);
    ServerHandler serverHandler = injector.getInstance(ServerHandler.class);
    System.out.println("Connecting...");
    client.injectDependencies(serverHandler);
    client.connect();
  }
}
