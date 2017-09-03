package com.github.jakz.romlib.support.header;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import com.pixbits.lib.io.archive.handles.Handle;

public abstract class WrapperStreamHandle extends Handle
{
  protected final Handle handle;
  
  protected WrapperStreamHandle(Handle handle)
  {
    this.handle = handle;
  }
  
  @Override public Handle getVerifierHandle() { return this; }
  @Override public String toString() { return handle.toString(); }
  @Override public Path path() { return handle.path(); }
  @Override public String relativePath() { return handle.relativePath(); }
  @Override public String fileName() { return handle.fileName(); }
  @Override public String plainName() { return handle.plainName(); }
  @Override public String plainInternalName() { return handle.plainInternalName(); }
  @Override public void relocate(Path file) { handle.relocate(file); }
  @Override public Handle relocateInternal(String internalName) { return handle.relocateInternal(internalName); }
  @Override public boolean isArchive() { return handle.isArchive(); }
  @Override public String getInternalExtension() { return handle.getInternalExtension(); }

  public abstract InputStream getInputStream() throws IOException;

  @Override public long crc() { return -1; }
  @Override public long size() { return handle.size(); }
  @Override public long compressedSize() { return handle.compressedSize(); }

}
