package org.example;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;

public class FileVerifier {
  // hasher bersifat stateful, dan tidak dapat di clear
  private Hasher hasher;

  public FileVerifier() {
  }

  public boolean verifyHashedBytes(String signature) {
    boolean isVerified = this.hasher.hash().toString().equals(signature) ? true : false;
    return isVerified;
  }

  public void putBytes(byte[] bytes) {
    this.hasher.putBytes(bytes);
  }

  public void prepare() {
    this.hasher = Hashing.sha256().newHasher();
  }

  public void clear() {
    this.hasher = null;
  }
}
