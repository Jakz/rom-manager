package jack.rm.log;

import com.pixbits.lib.log.LogAttribute;
import com.pixbits.lib.log.LogScope;

public abstract class LogTarget implements LogAttribute
{
  public static enum Type
  {
    FILE,
    HANDLE,
    ROM,
    ROM_SET,
    PLUGIN,
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
    
    @Override public String toString() { return ""; }
  }
  
  public static class Rom extends LogTarget
  {
    private final com.github.jakz.romlib.data.game.Game rom;
    
    Rom(com.github.jakz.romlib.data.game.Game rom)
    {
      super(Type.ROM);
      this.rom = rom;
    }
    
    @Override
    public String toString() { return rom.getTitle(); }
  }
  
  public static class RomSet extends LogTarget
  {
    private final jack.rm.data.romset.GameSet set;
    
    RomSet(jack.rm.data.romset.GameSet set)
    {
      super(Type.ROM_SET);
      this.set = set;
    }
    
    @Override
    public String toString() { return set.toString(); }
  }
  
  public static class File extends LogTarget
  {
    private final java.nio.file.Path path;
    
    File(java.nio.file.Path path)
    {
      super(Type.FILE);
      this.path = path;
    }
    
    @Override
    public String toString() { return path.toString(); }
  }
  
  public static class Handle extends LogTarget
  {
    private final com.pixbits.lib.io.archive.handles.Handle handle;
    
    Handle(com.pixbits.lib.io.archive.handles.Handle handle)
    {
      super(Type.HANDLE);
      this.handle = handle;
    }
    
    @Override public String toString() { return handle.toString(); }
  }
  
  public static class Plugin extends LogTarget
  {
    private final com.pixbits.lib.plugin.Plugin plugin;
    
    Plugin(com.pixbits.lib.plugin.Plugin plugin)
    {
      super(Type.PLUGIN);
      this.plugin = plugin;
    }
    
    @Override
    public String toString() { return plugin.getInfo().getSimpleName(); }
  }
  
  public static LogTarget none() { return new None(); }
  public static LogTarget file(java.nio.file.Path file) { return new File(file); }
  public static LogTarget handle(com.pixbits.lib.io.archive.handles.Handle handle) { return new Handle(handle); }
  public static LogTarget rom(com.github.jakz.romlib.data.game.Game rom) { return new Rom(rom); }
  public static LogTarget romset(jack.rm.data.romset.GameSet set) { return new RomSet(set); }
  public static LogTarget plugin(com.pixbits.lib.plugin.Plugin plugin) { return new Plugin(plugin); }

}
