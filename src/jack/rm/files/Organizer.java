package jack.rm.files;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeSet;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.io.archive.handles.ArchiveHandle;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.data.romset.GameSet;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.renamer.PatternSetPlugin;

public class Organizer
{	
	private static final Logger logger = Log.getLogger(LogSource.ORGANIZER);
  private static DecimalFormat format;
	
	static
	{
		format = new DecimalFormat();
		format.applyPattern("0000");
	}
	
	public static String formatNumber(int index)
	{
		return format.format(index);
	}
	
	public static Set<Pattern> getPatterns(GameSet set)
	{
	  Set<Pattern> patterns = new TreeSet<Pattern>();
	  
	  Set<PatternSetPlugin> plugins = set.getSettings().plugins.getPlugins(PluginRealType.PATTERN_SET);
	  plugins.forEach( p -> p.getPatterns().forEach(patterns::add) );
	  
	  return patterns;
	}

	public static void organizeRomIfNeeded(Game rom)
	{	  
	  if (!rom.hasCorrectName())
	  {
	    renameRom(rom);
	    internalRenameRom(rom);
	  }
	  
	  if (!rom.hasCorrectName())
	  
	  if (!rom.hasCorrectFolder())
	    moveRom(rom);
	}
	
	public static void internalRenameRom(Game rom)
	{
	  if (!rom.hasCorrectInternalName())
	  {
	    Handle path = rom.getHandle();
	    String name = rom.getCorrectInternalName() + "." + path.getInternalExtension();
	    
	    if (true)
	      throw new UnsupportedOperationException("relocate internal name is not compatible with new handles");
	    
	    if (((ArchiveHandle)path).renameInternalFile(name))
	      rom.setHandle(path.relocateInternal(name));
	    else
	      logger.e(LogTarget.rom(rom), "Can't rename internal name of archive: "+path.path());
	  }
	}
	
	public static void renameRom(Game rom)
	{
    Handle romPath = rom.getHandle();
	  Path renameTo = romPath.path().getParent();
	  
	  //TODO: should fix extensions if wrong and crc is verified but now just keeps them
    renameTo = renameTo.resolve(rom.getCorrectName()+"."+romPath.getExtension());

    try
    {
      rom.move(renameTo);
    }
    catch (FileAlreadyExistsException e)
    {
      logger.e(LogTarget.rom(rom), "Can't rename file, already exists: "+e.getFile());
    }
    catch (Exception e)
    {
      e.printStackTrace();
      // TODO: handle and write on log
    }
	}
	
	public static void moveRom(Game rom)
	{
	  if (rom.status != GameStatus.MISSING)
    {     
      try
      {      
        Path finalPath = GameSet.current.getSettings().romsPath.resolve(rom.getCorrectFolder());
  
        if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
        {
          Files.createDirectories(finalPath);
          logger.i(LogTarget.none(), "Creating folder "+finalPath);
        }
        
        Handle romPath = rom.getHandle();
        Path newFile = finalPath.resolve(romPath.path().getFileName());
                
        if (!newFile.equals(romPath.path()) && Files.exists(newFile))
        {
          logger.e(LogTarget.rom(rom), "Cannot rename to "+newFile.toString()+", file exists");
        }
        else if (!newFile.equals(romPath.path()))
        {  
          rom.move(newFile);
          logger.e(LogTarget.rom(rom), "Moved rom to "+finalPath);
        }    
      }
      catch (Exception e)
      {
        //TODO: handle and log
        e.printStackTrace();
      }   
    } 
	}
}
