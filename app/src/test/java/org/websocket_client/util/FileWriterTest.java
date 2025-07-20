
package org.websocket_client.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileWriterTest {

  private AclHandler aclHandler;
  private FileWriter fileWriter;

  @BeforeEach
  void setUp() {
    aclHandler = mock(AclHandler.class);
    fileWriter = new FileWriter(aclHandler);
  }

  @Test
  void testCreateDirectoryAndHandleAcl_createsDirectory(@TempDir Path tempDir) {
    Path targetFile = tempDir.resolve("subdir/test.txt");
    FileAccessInfo fai = mock(FileAccessInfo.class);
    when(fai.getPath()).thenReturn(targetFile.toString());

    boolean result = fileWriter.createDirectoryAndHandleAcl(fai);

    Path parent = targetFile.getParent();
    assertTrue(result);
    assertTrue(Files.exists(parent));
    assertTrue(Files.isDirectory(parent));
  }

  @Test
  void testWriteFile_successfulWrite(@TempDir Path tempDir) throws IOException {
    Path targetFile = tempDir.resolve("output.txt");
    FileAccessInfo fai = mock(FileAccessInfo.class);
    when(fai.getPath()).thenReturn(targetFile.toString());

    byte[] chunk1 = "Hello ".getBytes();
    byte[] chunk2 = "World!".getBytes();

    FileChunkMetadata fcm = mock(FileChunkMetadata.class);
    when(fcm.getChunkCount()).thenReturn(2L);
    when(fcm.getMapOfChunks()).thenReturn(Map.of(
        0L, chunk1,
        1L, chunk2));

    boolean result = fileWriter.writeFile(fcm, fai);

    assertTrue(result);
    String content = Files.readString(targetFile);
    assertEquals("Hello World!", content);
  }

  @Test
  void testWriteFile_handlesIOException() {
    FileAccessInfo fai = mock(FileAccessInfo.class);
    when(fai.getPath()).thenReturn("/invalid/path/file.txt");

    FileChunkMetadata fcm = mock(FileChunkMetadata.class);
    when(fcm.getChunkCount()).thenReturn(1L);
    when(fcm.getMapOfChunks()).thenReturn(Map.of(0L, "fail".getBytes()));

    boolean result = fileWriter.writeFile(fcm, fai);
    assertFalse(result);
  }
}
