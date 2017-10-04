package com.github.jakz.romlib.json;

import com.pixbits.lib.io.archive.handles.Handle;

public class RomSavedState
{
  public Handle handle;
  
  public RomSavedState(Handle handle)
  {
    this.handle = handle;
  }
  
  public RomSavedState() { }
}
