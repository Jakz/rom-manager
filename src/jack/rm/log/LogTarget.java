package jack.rm.log;

import com.pixbits.lib.log.LogAttribute;

public abstract class LogTarget implements LogAttribute
{
  public static enum Type
  {
    FILE,
    HANDLE,
    GAME,
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
  
  public static class GameLog extends LogTarget
  {
    private final com.github.jakz.romlib.data.game.Game rom;
    
    GameLog(com.github.jakz.romlib.data.game.Game rom)
    {
      super(Type.GAME);
      this.rom = rom;
    }
    
    @Override
    public String toString() { return rom.getTitle(); }
  }
  
  public static class RomLog extends LogTarget
  {
    private final com.github.jakz.romlib.data.game.Rom rom;
    
    RomLog(com.github.jakz.romlib.data.game.Rom rom)
    {
      super(Type.ROM);
      this.rom = rom;
    }
    
    @Override
    public String toString() { return rom.name; }
  }
  
  public static class RomSet extends LogTarget
  {
    private final com.github.jakz.romlib.data.set.GameSet set;
    
    RomSet(com.github.jakz.romlib.data.set.GameSet set)
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
  public static LogTarget game(com.github.jakz.romlib.data.game.Game game) { return new GameLog(game); }
  public static LogTarget rom(com.github.jakz.romlib.data.game.Rom rom) { return new RomLog(rom); }
  public static LogTarget romset(com.github.jakz.romlib.data.set.GameSet set) { return new RomSet(set); }
  public static LogTarget plugin(com.pixbits.lib.plugin.Plugin plugin) { return new Plugin(plugin); }

}
