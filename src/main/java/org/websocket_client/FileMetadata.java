package org.websocket_client;

import java.nio.file.attribute.AclEntryPermission;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FileMetadata {
  long fileSize;
  long chunkSize;
  long chunkCount;

  String filename;
  String owner;
  String signature;
  Set<AclEntryPermission> aclEntry;

  public FileMetadata(
      @JsonProperty("chunkSize") long chunkSize,
      @JsonProperty("chunkCount") long chunkCount,
      @JsonProperty("filename") String filename,
      @JsonProperty("signature") String signature,
      @JsonProperty("owner") String owner) {
    this.chunkSize = chunkSize;
    this.chunkCount = chunkCount;
    this.filename = filename;
    this.signature = signature;
    this.owner = owner;
  }

  // public FileMetadata(
  // @JsonProperty("fileSize") long fileSize,
  // @JsonProperty("chunkSize") long chunkSize,
  // @JsonProperty("chunkCount") long chunkCount,
  // @JsonProperty("filename") String filename,
  // @JsonProperty("owner") String owner,
  // @JsonProperty("signature") String signature,
  // @JsonProperty("aclEntry") Set<AclEntryPermission> aclEntry) {
  // this.fileSize = fileSize;
  // this.chunkSize = chunkSize;
  // this.chunkCount = chunkCount;
  // this.filename = filename;
  // this.owner = owner;
  // this.signature = signature;
  // this.aclEntry = aclEntry;
  // }
  //
  // public FileMetadata(
  // @JsonProperty("chunkSize") long chunkSize,
  // @JsonProperty("chunkCount") long chunkCount,
  // @JsonProperty("filename") String filename,
  // @JsonProperty("owner") String owner,
  // @JsonProperty("signature") String signature,
  // @JsonProperty("aclEntry") Set<AclEntryPermission> aclEntry) {
  // this.chunkSize = chunkSize;
  // this.chunkCount = chunkCount;
  // this.filename = filename;
  // this.owner = owner;
  // this.signature = signature;
  // this.aclEntry = aclEntry;
  // }

  public FileMetadata() {

  }

  public long getFileSize() {
    return this.fileSize;
  }

  public void setFileSize(long fileSize) {
    this.fileSize = fileSize;
  }

  public long getChunkSize() {
    return this.chunkSize;
  }

  public void setChunkSize(long chunkSize) {
    this.chunkSize = chunkSize;
  }

  public long getChunkCount() {
    return this.chunkCount;
  }

  public void setChunkCount(long chunkCount) {
    this.chunkCount = chunkCount;
  }

  public String getFileName() {
    return this.filename;
  }

  public void setFileName(String filename) {
    this.filename = filename;
  }

  public String getOwner() {
    return this.owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public String getSignature() {
    return this.signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public Set<AclEntryPermission> getAclEntry() {
    return this.aclEntry;
  }

  public void setAclEntry(Set<AclEntryPermission> aclEntry) {
    this.aclEntry = aclEntry;
  }
}
