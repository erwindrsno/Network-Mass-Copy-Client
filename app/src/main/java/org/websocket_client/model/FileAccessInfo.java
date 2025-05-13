package org.websocket_client.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonDeserialize(builder = FileAccessInfo.FileAccessInfoBuilder.class)
public class FileAccessInfo {
  @JsonProperty("id")
  int id;

  @JsonProperty("path")
  String path;

  @JsonProperty("owner")
  String owner;

  @JsonProperty("permissions")
  int permissions;

  @JsonProperty("ip_address")
  String ip_address;

  @JsonProperty("file_name")
  String filename;
}
