package org.websocket_client.util;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.*;

class FileRemoverTest {
  private FileRemover remover;

  @BeforeEach
  void setUp() {
    remover = new FileRemover();
  }

  @Test
  void testDeleteFile_existingFile_shouldReturnTrue() throws IOException {
    Path tempFile = Files.createTempFile("test-file", ".tmp");

    assertTrue(Files.exists(tempFile));
    assertTrue(remover.deleteFile(tempFile));
    assertFalse(Files.exists(tempFile));
  }

  @Test
  void testDeleteFile_nonExistingFile_shouldReturnTrue() {
    Path fakePath = Paths.get("nonexistent_file_" + System.nanoTime());
    assertTrue(remover.deleteFile(fakePath));
  }

  @Test
  void testDeleteDirectoryRecursively_withFiles_shouldDeleteAll() throws IOException {
    Path tempDir = Files.createTempDirectory("test-dir");
    Path nestedDir = Files.createDirectory(tempDir.resolve("nested"));
    Files.createTempFile(nestedDir, "file-", ".txt");
    Files.createTempFile(tempDir, "file-", ".txt");

    assertTrue(Files.exists(tempDir));
    assertTrue(remover.deleteDirectoryRecursively(tempDir));
    assertFalse(Files.exists(tempDir));
  }

  @Test
  void testDeleteDirectoryRecursively_nonExistingDir_shouldReturnTrue() {
    Path fakePath = Paths.get("nonexistent_dir_" + System.nanoTime());
    assertTrue(remover.deleteDirectoryRecursively(fakePath));
  }
}
