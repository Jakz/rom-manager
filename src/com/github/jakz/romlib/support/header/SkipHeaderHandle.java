package com.github.jakz.romlib.support.header;

import java.io.IOException;
import java.io.InputStream;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.pixbits.lib.io.SkippingInputStream;
import com.pixbits.lib.io.archive.handles.Handle;

public class SkipHeaderHandle extends WrapperStreamHandle
{
  Rule rule;
  
  public SkipHeaderHandle(JsonObject o, JsonDeserializationContext context)
  {
    super(o, context);
    rule = Rule.of(o.get("bytesToSkip").getAsInt());
  }

  public SkipHeaderHandle(Handle handle, Rule rule)
  {
    super(handle);
    this.rule = rule;
  }
  
  public void serializeToJson(JsonObject j, JsonSerializationContext context)
  {
    super.serializeToJson(j, context);
    j.addProperty("bytesToSkip", rule.bytesToSkip);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new SkippingInputStream(handle.getInputStream(), rule.bytesToSkip);
  }
  
  @Override
  public long size() { return super.size() - rule.bytesToSkip; }

}
