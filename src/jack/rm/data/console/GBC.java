package jack.rm.data.console;

import jack.rm.data.rom.RomSave;

public class GBC
{
  public static class Save implements RomSave<Save.Type>
  {
    public static enum Type implements RomSave.Type
    {
      NONE,
    };
    
    private final Type type;
    private final long size;
    
    public Save(Type type, long size)
    {
      this.type = type;
      this.size = size;
    }
    
    public Type getType() { return type; }
    public long getSize() { return size; }
  }
}
