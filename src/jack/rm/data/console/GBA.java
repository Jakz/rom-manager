package jack.rm.data.console;

import jack.rm.data.rom.RomSave;
import jack.rm.data.rom.RomSize;

public class GBA
{
  public static class Save implements RomSave<Save.Type>
  {
    public static interface Version { };
    
    public static enum Type implements RomSave.Type
    {
      EEPROM,
      FLASH,
      SRAM,
      TBC,
      NONE;
    };
    
    private final Type type;
    private long size;
    private Version version;
    
    public Save(Type type)
    {
      this.type = type;
    }
    
    public Save(Type type, Version version)
    {
      this(type);
      this.version = version;
    }
    
    public Save(Type type, Version version, int size)
    {
      this(type, version);
      this.size = size;
    }
    
    public String toString()
    {
      String value = type.toString();
      
      if (version != null)
        value += " "+version;
      
      if (size != 0)
        value += " ("+RomSize.forBytes(size, false)+")";
      
      return value;
    }
    
    public long getSize() { return size; }
    public Version getVersion() { return version; }
    public Type getType() { return type; }
    
    public static Version[] valuesForType(Type type)
    {
      if (type == Type.NONE) return null;
      else if (type == Type.FLASH) return Flash.values();
      else if (type == Type.EEPROM) return EEPROM.values();
      else if (type == Type.SRAM) return SRAM.values();
      else return null;
    }
    
    public enum SRAM implements Version
    {
      v100,
      v102,
      v103,
      v110,
      v111,
      v112,
      v113
    }
    
    public enum EEPROM implements Version
    {
      v111,
      v120,
      v121,
      v122,
      v124,
      v125,
      v126
    }
    
    public enum Flash implements Version
    {
      v102,
      v103,
      v120,
      v121,
      v123,
      v124,
      v125,
      v126,
      v130,
      v131,
      v133
    }
  }
}
