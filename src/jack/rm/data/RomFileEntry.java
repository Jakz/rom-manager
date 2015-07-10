package jack.rm.data;

import java.io.*;
import com.google.gson.*;
import java.lang.reflect.Type;

public abstract class RomFileEntry
{
  public final EntryType type;
  public abstract String toString();
  public abstract File file();
  public abstract RomFileEntry build(File file);
  
  RomFileEntry(EntryType type)
  {
    this.type = type;
  }
  
  public static enum EntryType
  {
    BIN,
    ARCHIVE
  };

  public static class Bin extends RomFileEntry
  {
    public final File file;

    public Bin(File file)
    {
      super(EntryType.BIN);
      this.file = file;
    }
    
    public File file() { return file; }
    public String toString() { return file.getAbsolutePath(); }
    
    public RomFileEntry build(File file)
    {
      return new Bin(file);
    }
  }
  
  public static class Archive extends RomFileEntry
  {
    public final File file;
    public final String internalName;
    
    public Archive(File file, String internalName)
    {
      super(EntryType.ARCHIVE);
      this.file = file;
      this.internalName = internalName;
    }
    
    public File file() { return file; }
    public String toString() { return file.getAbsolutePath() + " ("+internalName+")"; }
    
    public RomFileEntry build(File file)
    {
      return new Archive(file, this.internalName);
    }
  }
  
  public static class Adapter implements JsonDeserializer<RomFileEntry>, JsonSerializer<RomFileEntry>
  {
    @Override
    public RomFileEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
      JsonObject obj = json.getAsJsonObject();
      
      EntryType type = (EntryType)context.deserialize(obj.get("type"), EntryType.class);
      
      if (type != null)
      {
        if (type == EntryType.BIN)
          return new Bin(new File((String)context.deserialize(obj.get("file"), String.class)));
        else if (type == EntryType.ARCHIVE)
          return new Archive(new File((String)context.deserialize(obj.get("file"), String.class)), obj.get("internalName").getAsString());
      }      
      return null;
    }
    
    @Override
    public JsonElement serialize(RomFileEntry entry, Type typeOfT, JsonSerializationContext context)
    {
      JsonObject json = new JsonObject();
      EntryType entryType = entry.type;
      json.add("type", context.serialize(entryType, EntryType.class));
      
      switch (entryType)
      {
        case BIN:
        {
          json.add("file", context.serialize(entry.file().getAbsolutePath(), String.class));
          break;
        }
        case ARCHIVE:
        {
          json.add("file", context.serialize(entry.file().getAbsolutePath(), String.class));
          json.add("internalName", context.serialize(((Archive)entry).internalName, String.class));
          break;
        }
      }
      
      return json;
    }
    
    // serve il serializer
  }
}
