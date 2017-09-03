package com.github.jakz.romlib.support.header;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.handles.Handle;

public class Signature
{
  private final byte[] data;
  private final int offset;
  
  public Signature(int offset, byte[] data)
  {
    this.offset = offset;
    this.data = data;
  }
  
  public Signature(byte[] data)
  {
    this.offset = 0;
    this.data = data;
  }
  
  public boolean verify(VerifierEntry handle) throws IOException
  {
    if (!handle.isSingleVerifierEntry())
      throw new UnsupportedOperationException("Signature.verify can't accept a multiple VerifyEntry as argument");
    
    /* TODO: only offset zero is handled */
    byte[] buffer = new byte[data.length];

    try (InputStream is = handle.getVerifierHandle().getInputStream())
    {    
      
      int r = 0;
      
      /* read data.length bytes */
      while (r < data.length)
      {
        /* read up to missing bytes to check signature */
        int c = is.read(buffer, r, data.length - r);
        
        /* if EOS is reached before then return false */
        if (c == -1) return false;
      }
    }
    
    return Arrays.equals(data, buffer);
  }
  
  public static Signature of(byte[] bytes) { return new Signature(bytes); }
  public static Signature of(String string) { return new Signature(string.getBytes()); }
}
