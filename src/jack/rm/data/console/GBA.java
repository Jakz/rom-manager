package jack.rm.data.console;

import jack.rm.data.RomSave;
import jack.rm.data.RomSize;

public class GBA
{
  public static class Save implements RomSave<Save.Type>
  {
    public static enum Type implements RomSave.Type
    {
      EEPROM,
      FLASH,
      SRAM,
      NONE
    };
    
    private final Type type;
    private long size;
    private int version;
    
    public Save(Type type)
    {
      this.type = type;
    }
    
    public Save(Type type, int version)
    {
      this(type);
      this.version = version;
    }
    
    public Save(Type type, int version, int size)
    {
      this(type, version);
      this.size = size;
    }
    
    public String toString()
    {
      String value = type.toString();
      
      if (version != 0)
        value += " v"+version;
      
      if (size != 0)
        value += " ("+RomSize.forBytes(size, false)+")";
      
      return value;
    }
    
    public int getVersion() { return version; }
    public Type getType() { return type; }
  }
}
