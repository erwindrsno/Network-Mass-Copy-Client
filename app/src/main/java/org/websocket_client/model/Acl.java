package org.websocket_client.model;

import java.nio.file.attribute.AclEntryPermission;
import java.util.Set;

public class Acl {
  static public Set<AclEntryPermission> getReadAcl() {
    return readPermissions;
  }

  static public Set<AclEntryPermission> getWriteAcl() {
    return writePermissions;
  }

  static public Set<AclEntryPermission> getExecuteAcl() {
    return executePermissions;
  }

  static public Set<AclEntryPermission> getRWXAcl() {
    return rwxPermissions;
  }

  static public Set<AclEntryPermission> getReadWriteAcl(){
    return readWritePermissions;
  }

  static public Set<AclEntryPermission> getReadExecuteAcl(){
    return readExecutePermissions;
  }

  static public Set<AclEntryPermission> getAdminAcl() {
    return fullControl;
  }

  static public Set<AclEntryPermission> getUserAcl() {
    return userControl;
  }

  static private Set<AclEntryPermission> fullControl = Set.of(
      AclEntryPermission.READ_DATA,
      AclEntryPermission.READ_ACL,
      AclEntryPermission.READ_ATTRIBUTES,
      AclEntryPermission.READ_NAMED_ATTRS,
      AclEntryPermission.WRITE_DATA,
      AclEntryPermission.APPEND_DATA,
      AclEntryPermission.WRITE_ATTRIBUTES,
      AclEntryPermission.WRITE_NAMED_ATTRS,
      AclEntryPermission.DELETE,
      AclEntryPermission.DELETE_CHILD,
      AclEntryPermission.EXECUTE,
      AclEntryPermission.WRITE_ACL,
      AclEntryPermission.WRITE_OWNER,
      AclEntryPermission.SYNCHRONIZE);

  static private Set<AclEntryPermission> userControl = Set.of(
      AclEntryPermission.READ_DATA,
      AclEntryPermission.READ_ACL,
      AclEntryPermission.READ_ATTRIBUTES,
      AclEntryPermission.READ_NAMED_ATTRS,
      AclEntryPermission.WRITE_DATA,
      AclEntryPermission.APPEND_DATA,
      AclEntryPermission.WRITE_ATTRIBUTES,
      AclEntryPermission.WRITE_NAMED_ATTRS,
      AclEntryPermission.DELETE_CHILD,
      AclEntryPermission.EXECUTE,
      AclEntryPermission.SYNCHRONIZE);

  static private Set<AclEntryPermission> rwxPermissions = Set.of(
      AclEntryPermission.READ_DATA,
      AclEntryPermission.READ_ACL,
      AclEntryPermission.READ_ATTRIBUTES,
      AclEntryPermission.READ_NAMED_ATTRS,
      AclEntryPermission.WRITE_DATA,
      AclEntryPermission.APPEND_DATA,
      AclEntryPermission.WRITE_ATTRIBUTES,
      AclEntryPermission.WRITE_NAMED_ATTRS,
      AclEntryPermission.DELETE,
      AclEntryPermission.DELETE_CHILD,
      AclEntryPermission.EXECUTE);

  static private Set<AclEntryPermission> readPermissions = Set.of(
      AclEntryPermission.READ_DATA,
      AclEntryPermission.READ_ACL,
      AclEntryPermission.READ_ATTRIBUTES,
      AclEntryPermission.READ_NAMED_ATTRS);

  static private Set<AclEntryPermission> writePermissions = Set.of(
      AclEntryPermission.WRITE_DATA,
      AclEntryPermission.APPEND_DATA,
      AclEntryPermission.WRITE_ATTRIBUTES,
      AclEntryPermission.WRITE_NAMED_ATTRS,
      AclEntryPermission.DELETE,
      AclEntryPermission.DELETE_CHILD);

  static private Set<AclEntryPermission> executePermissions = Set.of(
      AclEntryPermission.EXECUTE);

  static private Set<AclEntryPermission> readWritePermissions = Set.of(
      AclEntryPermission.READ_DATA,
      AclEntryPermission.READ_ACL,
      AclEntryPermission.READ_ATTRIBUTES,
      AclEntryPermission.READ_NAMED_ATTRS,
      AclEntryPermission.WRITE_DATA,
      AclEntryPermission.APPEND_DATA,
      AclEntryPermission.WRITE_ATTRIBUTES,
      AclEntryPermission.WRITE_NAMED_ATTRS,
      AclEntryPermission.DELETE_CHILD,
      AclEntryPermission.SYNCHRONIZE
  );

    static private Set<AclEntryPermission> readExecutePermissions = Set.of(
      AclEntryPermission.READ_DATA,
      AclEntryPermission.READ_ACL,
      AclEntryPermission.READ_ATTRIBUTES,
      AclEntryPermission.READ_NAMED_ATTRS,
      AclEntryPermission.EXECUTE,
      AclEntryPermission.SYNCHRONIZE
  );
}
