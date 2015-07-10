package jack.rm.log;

public abstract class LogTarget
{
  public static enum Type
  {
    FILE,
    ROM,
    ROM_SET,
    NONE
  };
  
  public final Type type;
  
  LogTarget(Type type)
  {
    this.type = type;
  }
  
  public static class None extends LogTarget
  {
    None()
    {
      super(Type.NONE);
    }
  }
  
  public static class Rom extends LogTarget
  {
    private final jack.rm.data.Rom rom;
    
    Rom(jack.rm.data.Rom rom)
    {
      super(Type.ROM);
      this.rom = rom;
    }
    
    public String toString() { return rom.title; }
  }
  
  public static class RomSet extends LogTarget
  {
    private final jack.rm.data.set.RomSet set;
    
    RomSet(jack.rm.data.set.RomSet set)
    {
      super(Type.ROM_SET);
      this.set = set;
    }
    
    public String toString() { return set.toString(); }
  }
  
  public static class File extends LogTarget
  {
    private final java.io.File file;
    
    File(java.io.File file)
    {
      super(Type.FILE);
      this.file = file;
    }
    
    public String toString() { return file.getName(); }
  }
  
  public static LogTarget none() { return new None(); }
  public static LogTarget file(java.io.File file) { return new File(file); }
  public static LogTarget rom(jack.rm.data.Rom rom) { return new Rom(rom); }
  public static LogTarget romset(jack.rm.data.set.RomSet set) { return new RomSet(set); }

}
