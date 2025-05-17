package org.websocket_client.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.Client;
import org.websocket_client.model.Acl;
import org.websocket_client.model.FileAccessInfo;
import org.websocket_client.model.FileChunkMetadata;

import com.google.inject.Inject;

public class FileWriter {
  private Logger logger;

  @Inject
  public FileWriter() {
    this.logger = LoggerFactory.getLogger(Client.class);
  }

  public boolean writeFile(FileChunkMetadata fcm, FileAccessInfo fai) {
    try {
      Path path = Path.of(fai.getPath());
      Path parent = path.getParent();

      // Delete the file if it exists
      Files.deleteIfExists(path);

      if (parent != null) {
        // Check if parent exists and is a directory
        if (Files.exists(parent) && Files.isDirectory(parent)) {
          logger.info("Parent is a directory.");
        } else {
          logger.info("Parent is not a directory or does not exist. Creating it...");
          Files.createDirectories(parent);
          this.handleAcl(parent, fai);
        }
      }

      for (long i = 0; i < fcm.getChunkCount(); i++) {
        if (i == fcm.getChunkCount() - 1) {
          this.logger.info("The file successfully written!");
        }
        Files.write(path, fcm.getMapOfChunks().get(i), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      }
      return true;

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return false;
    }
  }

  public void handleAcl(Path path, FileAccessInfo fai) throws Exception {
    logger.info("Handling ACL");
    AclFileAttributeView view = Files.getFileAttributeView(path,
        AclFileAttributeView.class);

    List<AclEntry> acl = view.getAcl();
    List<AclEntry> oldAcl = List.copyOf(acl);

    Set<AclEntryPermission> adminPermissions = Acl.getAdminAcl();
    Set<AclEntryPermission> userPermissions = Acl.getUserAcl();
    Set<AclEntryPermission> executePermissions = Acl.getExecuteAcl();

    for (int i = 0; i < acl.size(); i++) {
      AclEntry oldEntry = oldAcl.get(i);

      AclEntry newEntry = AclEntry.newBuilder()
          .setType(AclEntryType.ALLOW)
          .setPrincipal(oldEntry.principal())
          .setPermissions(adminPermissions)
          .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
          .build();

      acl.set(i, newEntry);
      view.setAcl(acl);
    }

    UserPrincipal administrator = path.getFileSystem().getUserPrincipalLookupService()
        .lookupPrincipalByName("ftis\\administrator");

    AclEntry adminEntry = AclEntry.newBuilder()
        .setType(AclEntryType.ALLOW)
        .setPrincipal(administrator)
        .setPermissions(adminPermissions)
        .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
        .build();

    acl.add(0, adminEntry); // insert before any DENY entries

    UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService()
        .lookupPrincipalByName("ftis\\" + fai.getOwner());

    AclEntry entry = AclEntry.newBuilder()
        .setType(AclEntryType.ALLOW)
        .setPrincipal(user)
        .setPermissions(userPermissions)
        .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
        .build();

    acl.add(acl.size() - 1, entry); // insert before any DENY entries

    view.setAcl(acl);
  }
}
