package jack.rm.data.console;

import jack.rm.data.rom.RomSave;
import jack.rm.data.rom.RomSize;

public class NDS
{
  public static class Save implements RomSave<Save.Type>
  {
    public static enum Type implements RomSave.Type
    {
      EEPROM,
      FLASH,
      TBC,
      NONE
    };
    
    private final Type type;
    private long size;
    
    public Save(Type type)
    {
      this.type = type;
    }

    public Save(Type type, long size)
    {
      this(type);
      this.size = size;
    }
    
    public String toString()
    {
      if (size != 0)
      {
        RomSize size = RomSize.forBytes(this.size, false);
        return String.format("%s (%s)", type.toString(), size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BITS));
      }
      else
        return type.toString();
    }
    
    public Type getType() { return type; }
  }
}
