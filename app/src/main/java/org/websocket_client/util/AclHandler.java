package org.websocket_client.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.model.Acl;
import org.websocket_client.model.FileAccessInfo;

public class AclHandler {
  private final Set<AclEntryPermission> adminPermissions = Acl.getAdminAcl();
  private final Set<AclEntryPermission> userPermissions = Acl.getUserAcl();
  private final Set<AclEntryPermission> readPermissions = Acl.getReadAcl();
  private final Set<AclEntryPermission> readWritePermissions = Acl.getReadWriteAcl();
  private final Set<AclEntryPermission> readExecutePermissions = Acl.getReadExecuteAcl();

  private Logger logger;

  public AclHandler() {
    this.logger = LoggerFactory.getLogger(AclHandler.class);
  }

  public void handleCopyAcl(Path path, FileAccessInfo fai) {
    try {
      AclFileAttributeView view = Files.getFileAttributeView(path,
          AclFileAttributeView.class);
      List<AclEntry> acl = view.getAcl();

      this.removeInherittedPermissions(view);

      UserPrincipal administrator = path.getFileSystem().getUserPrincipalLookupService()
          .lookupPrincipalByName("ftis\\administrator");
      UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService()
          .lookupPrincipalByName("ftis\\" + fai.getOwner());

      AclEntry adminEntry = buildAclEntry(administrator, this.adminPermissions, true);
      if (adminEntry != null) {
        acl.add(0, adminEntry);
      }

      Set<AclEntryPermission> targetPermissions = resolveEntryPermissionBits(fai.getPermissions());
      AclEntry userEntry = buildAclEntry(user, targetPermissions, true);
      if (userEntry != null) {
        acl.add(userEntry);
      }

      view.setAcl(acl);
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

  public boolean handleTakeownAcl(Path path, String owner) {
    try {
      AclFileAttributeView view = Files.getFileAttributeView(path,
          AclFileAttributeView.class);

      UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService()
          .lookupPrincipalByName(owner);

      List<AclEntry> acl = view.getAcl();

      acl.removeIf(entry -> user.equals(entry.principal()));

      AclEntry userDenyEntry = this.buildAclEntry(user, this.userPermissions, false);

      acl.add(0, userDenyEntry);
      view.setAcl(acl);

      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  protected AclEntry buildAclEntry(UserPrincipal userPrincipal,
      Set<AclEntryPermission> aclEntryPermission, boolean allowType) {
    try {
      AclEntryType aclEntryType = allowType ? AclEntryType.ALLOW : AclEntryType.DENY;

      return AclEntry.newBuilder()
          .setPrincipal(userPrincipal)
          .setType(aclEntryType)
          .setPermissions(aclEntryPermission)
          .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
          .build();

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      return null;
    }
  }

  protected Set<AclEntryPermission> resolveEntryPermissionBits(String permissions) {
    int permBit = Integer.parseInt(permissions, 2);
    switch (permBit) {
      case 4:
        return this.readPermissions;
      case 5:
        return this.readExecutePermissions;
      case 6:
        return this.readWritePermissions;
      case 7:
        return this.userPermissions;
      default:
        logger.warn("Invalid permission bit: {}", permissions);
        return Set.of();
    }
  }

  protected void removeInherittedPermissions(AclFileAttributeView view) {
    try {
      List<AclEntry> acl = view.getAcl();
      List<AclEntry> oldAcl = List.copyOf(acl);

      for (int i = 0; i < acl.size(); i++) {
        AclEntry oldEntry = oldAcl.get(i);

        AclEntry newEntry = buildAclEntry(
            oldEntry.principal(),
            oldEntry.permissions(),
            true);

        if (newEntry != null) {
          acl.set(i, newEntry);
        }
      }
      // view.setAcl(acl);
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
  }
}
