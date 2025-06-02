package org.websocket_client.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@JsonDeserialize(builder = FileChunkMetadata.FileChunkMetadataBuilder.class)
public class FileChunkMetadata {
  @JsonProperty("uuid")
  String uuid;

  @JsonProperty("chunkCount")
  long chunkCount;

  @JsonProperty("fileName")
  String filename;

  @JsonProperty("signature")
  String signature;

  @JsonIgnore
  Map<Long, byte[]> mapOfChunks;
}
