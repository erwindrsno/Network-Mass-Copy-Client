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
@JsonDeserialize(builder = DirectoryAccessInfo.DirectoryAccessInfoBuilder.class)
public class DirectoryAccessInfo {
  @JsonProperty("id")
  Integer id;

  @JsonProperty("path")
  String path;

  @JsonProperty("owner")
  String owner;
}
