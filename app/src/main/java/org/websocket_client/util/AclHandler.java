package org.websocket_client.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.websocket_client.model.Acl;
import org.websocket_client.model.FileAccessInfo;

public class AclHandler {
  private Set<AclEntryPermission> adminPermissions = Acl.getAdminAcl();
  private Set<AclEntryPermission> userPermissions = Acl.getUserAcl();
  private Set<AclEntryPermission> readPermissions = Acl.getReadAcl();
  private Set<AclEntryPermission> readWritePermissions = Acl.getReadWriteAcl();
  private Set<AclEntryPermission> readExecutePermissions = Acl.getReadExecuteAcl();

  private Logger logger;

  public AclHandler() {
    this.logger = LoggerFactory.getLogger(AclHandler.class);
  }

  public void handleCopyAcl(Path path, FileAccessInfo fai) {
    try {
      AclFileAttributeView view = Files.getFileAttributeView(path,
          AclFileAttributeView.class);

      List<AclEntry> acl = view.getAcl();
      List<AclEntry> oldAcl = List.copyOf(acl);

      for (int i = 0; i < acl.size(); i++) {
        AclEntry oldEntry = oldAcl.get(i);

        AclEntry newEntry = AclEntry.newBuilder()
            .setType(AclEntryType.ALLOW)
            .setPrincipal(oldEntry.principal())
            .setPermissions(this.adminPermissions)
            .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
            .build();

        acl.set(i, newEntry);
        view.setAcl(acl);
      }

      String permission = fai.getPermissions();
      Set<AclEntryPermission> targetPermissions = new HashSet<>();
      int permBit = Integer.parseInt(permission, 2);

        switch (permBit) {
            case 4: targetPermissions = this.readPermissions; break;
            case 5: targetPermissions = this.readExecutePermissions; break;
            case 6: targetPermissions = this.readWritePermissions; break;
            case 7: targetPermissions = this.userPermissions; break;
            default: logger.info("Invalid permission"); break;
        }

      UserPrincipal administrator = path.getFileSystem().getUserPrincipalLookupService()
          .lookupPrincipalByName("erwin");

      // UserPrincipal administrator =
      // path.getFileSystem().getUserPrincipalLookupService()
      // .lookupPrincipalByName("ftis\\administrator");

      AclEntry adminEntry = AclEntry.newBuilder()
          .setType(AclEntryType.ALLOW)
          .setPrincipal(administrator)
          .setPermissions(this.adminPermissions)
          .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
          .build();

      acl.add(0, adminEntry);

      UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService()
          .lookupPrincipalByName(fai.getOwner());

      AclEntry userEntry = AclEntry.newBuilder()
          .setType(AclEntryType.ALLOW)
          .setPrincipal(user)
          .setPermissions(this.readExecutePermissions)
          .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
          .build();

      acl.add(acl.size() - 1, userEntry);

      view.setAcl(acl);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean handleTakeownAcl(Path path, String owner) {
    try {
      System.out.println("the parent is: " + path.getParent());

      AclFileAttributeView view = Files.getFileAttributeView(path,
          AclFileAttributeView.class);

      UserPrincipal user = path.getFileSystem().getUserPrincipalLookupService()
          .lookupPrincipalByName(owner);

      List<AclEntry> acl = view.getAcl();
      ListIterator<AclEntry> iterator = acl.listIterator();

      while (iterator.hasNext()) {
        AclEntry entry = iterator.next();
        if (entry.principal().equals(user)) {
          iterator.remove(); // remove old entry
        }
      }

      AclEntry denyEntry = AclEntry.newBuilder()
          .setPrincipal(user)
          .setType(AclEntryType.DENY)
          .setPermissions(this.userPermissions)
          .setFlags(AclEntryFlag.FILE_INHERIT, AclEntryFlag.DIRECTORY_INHERIT)
          .build();

      acl.add(0, denyEntry);
      view.setAcl(acl);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }
}
