package com.github.jakz.romlib.support.header;

import java.io.IOException;
import java.io.InputStream;

import com.pixbits.lib.io.SkippingInputStream;
import com.pixbits.lib.io.archive.handles.Handle;

public class SkipHeaderHandle extends WrapperStreamHandle
{
  Rule rule;
  
  public SkipHeaderHandle(Handle handle, Rule rule) {
    super(handle);
    this.rule = rule;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new SkippingInputStream(handle.getInputStream(), rule.bytesToSkip);
  }

}
