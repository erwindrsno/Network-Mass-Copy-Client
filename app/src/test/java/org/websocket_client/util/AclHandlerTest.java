package org.websocket_client.util;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.websocket_client.model.FileAccessInfo;

public class AclHandlerTest {

  private AclHandler aclHandler;

  @BeforeEach
  void setup() {
    aclHandler = new AclHandler();
  }

  @Test
  void testHandleCopyAcl() throws Exception {
    Path mockPath = mock(Path.class);
    FileAccessInfo fai = mock(FileAccessInfo.class);
    AclFileAttributeView mockView = mock(AclFileAttributeView.class);
    FileSystem fs = mock(FileSystem.class);
    UserPrincipalLookupService lookup = mock(UserPrincipalLookupService.class);
    UserPrincipal adminPrincipal = mock(UserPrincipal.class);
    UserPrincipal userPrincipal = mock(UserPrincipal.class);

    when(fai.getOwner()).thenReturn("testuser");
    when(fai.getPermissions()).thenReturn("111"); // binary 7

    when(mockPath.getFileSystem()).thenReturn(fs);
    when(fs.getUserPrincipalLookupService()).thenReturn(lookup);
    when(lookup.lookupPrincipalByName("ftis\\administrator")).thenReturn(adminPrincipal);
    when(lookup.lookupPrincipalByName("ftis\\testuser")).thenReturn(userPrincipal);

    List<AclEntry> aclEntries = new ArrayList<>();
    when(mockView.getAcl()).thenReturn(aclEntries);

    // Mock static Files.getFileAttributeView
    try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.getFileAttributeView(mockPath, AclFileAttributeView.class))
          .thenReturn(mockView);

      aclHandler.handleCopyAcl(mockPath, fai);

      verify(mockView).setAcl(anyList());
    }
  }

  @Test
  void testHandleTakeownAcl() throws Exception {
    Path mockPath = mock(Path.class);
    AclFileAttributeView mockView = mock(AclFileAttributeView.class);
    FileSystem fs = mock(FileSystem.class);
    UserPrincipalLookupService lookup = mock(UserPrincipalLookupService.class);
    UserPrincipal userPrincipal = mock(UserPrincipal.class);

    when(mockPath.getFileSystem()).thenReturn(fs);
    when(fs.getUserPrincipalLookupService()).thenReturn(lookup);
    when(lookup.lookupPrincipalByName("testuser")).thenReturn(userPrincipal);

    List<AclEntry> aclEntries = new ArrayList<>();
    aclEntries.add(mock(AclEntry.class)); // dummy entry
    when(mockView.getAcl()).thenReturn(aclEntries);

    try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
      mockedFiles.when(() -> Files.getFileAttributeView(mockPath, AclFileAttributeView.class))
          .thenReturn(mockView);

      boolean result = aclHandler.handleTakeownAcl(mockPath, "testuser");
      assert result;

      verify(mockView).setAcl(anyList());
    }
  }

  @Test
  void testResolveEntryPermissionBits_validBits() {
    Set<AclEntryPermission> permissions = aclHandler.resolveEntryPermissionBits("100"); // 4
    assert permissions != null && !permissions.isEmpty();
  }

  @Test
  void testResolveEntryPermissionBits_invalidBits() {
    Set<AclEntryPermission> permissions = aclHandler.resolveEntryPermissionBits("000"); // 0
    assert permissions.isEmpty();
  }
}
