package ws.client;

import java.nio.file.attribute.AclEntryPermission;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FileMetadata{
    long fileSize;
    long chunkSize;

    String fileName;
    String user;
    String signature;
    Set<AclEntryPermission> aclEntry;

    public FileMetadata(
        @JsonProperty("fileSize") long fileSize, 
        @JsonProperty("chunkSize") long chunkSize,
        @JsonProperty("fileName") String fileName, 
        @JsonProperty("user") String user,
        @JsonProperty("signature") String signature,
        @JsonProperty("aclEntry") Set<AclEntryPermission> aclEntry
    ){
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
        this.fileName = fileName;
        this.user = user;
        this.signature = signature;
        this.aclEntry = aclEntry;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public void setFileSize(long fileSize){
        this.fileSize = fileSize;
    }

    public long getChunkSize(){
        return this.chunkSize;
    }

    public void setChunkSize(long chunkSize){
        this.chunkSize = chunkSize;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getSignature(){
        return this.signature;
    }

    public void setSignature(String signature){
        this.signature = signature;
    }

    public Set<AclEntryPermission> getAclEntry(){
        return this.aclEntry;
    }

    public void setAclEntry(Set<AclEntryPermission> aclEntry){
        this.aclEntry = aclEntry;
    }
}
