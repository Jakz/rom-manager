package jack.rm.data;

import java.nio.file.*;
import com.google.gson.*;
import java.lang.reflect.Type;

public abstract class RomFileEntry
{
  public final RomType type;
  @Override
  public abstract String toString();
  public abstract Path file();
  public abstract String plainName();
  public abstract RomFileEntry build(Path file);

  
  RomFileEntry(RomType type)
  {
    this.type = type;
  }

  public static class Bin extends RomFileEntry
  {
    public final Path file;

    public Bin(Path file)
    {
      super(RomType.BIN);
      this.file = file;
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString(); }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().length()-4); }
    
    @Override
    public RomFileEntry build(Path file)
    {
      return new Bin(file);
    }
  }
  
  public static class Archive extends RomFileEntry
  {
    public final Path file;
    public final String internalName;
    
    public Archive(Path file, String internalName)
    {
      super(RomType.ZIP);
      this.file = file;
      this.internalName = internalName;
    }
    
    @Override
    public Path file() { return file; }
    @Override
    public String toString() { return file.getFileName().toString() + " ("+internalName+")"; }
    @Override
    public String plainName() { return file.getFileName().toString().substring(0, file.getFileName().toString().length()-4); }
    
    @Override
    public RomFileEntry build(Path file)
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
      
      RomType type = (RomType)context.deserialize(obj.get("type"), RomType.class);
      
      if (type != null)
      {
        if (type == RomType.BIN)
          return new Bin(java.nio.file.Paths.get((String)context.deserialize(obj.get("file"), String.class)));
        else if (type == RomType.ZIP)
          return new Archive(java.nio.file.Paths.get((String)context.deserialize(obj.get("file"), String.class)), obj.get("internalName").getAsString());
      }      
      return null;
    }
    
    @Override
    public JsonElement serialize(RomFileEntry entry, Type typeOfT, JsonSerializationContext context)
    {
      JsonObject json = new JsonObject();
      RomType entryType = entry.type;
      json.add("type", context.serialize(entryType, RomType.class));
      
      switch (entryType)
      {
        case BIN:
        {
          json.add("file", context.serialize(entry.file().toString(), String.class));
          break;
        }
        case ZIP:
        {
          json.add("file", context.serialize(entry.file().toString(), String.class));
          json.add("internalName", context.serialize(((Archive)entry).internalName, String.class));
          break;
        }
      }
      
      return json;
    }
    
    // serve il serializer
  }
}
