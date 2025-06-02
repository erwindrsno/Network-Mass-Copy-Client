package org.websocket_client;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {
  private String title;
  private List<FileMetadata> fileMetadataList;
  private Integer entryId;

  public Metadata(
      @JsonProperty("fileMetadataList") List<FileMetadata> fileMetadataList,
      @JsonProperty("title") String title,
      @JsonProperty("entry_id") Integer entryId) {
    this.fileMetadataList = fileMetadataList;
    this.title = title;
    this.entryId = entryId;
  }

  public List<FileMetadata> getFileMetadataList() {
    return this.fileMetadataList;
  }

  public void setFileMetadataList(List<FileMetadata> fileMetadataList) {
    this.fileMetadataList = fileMetadataList;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getEntryId() {
    return this.entryId;
  }

  public void setEntryId(Integer entryId) {
    this.entryId = entryId;
  }
}
