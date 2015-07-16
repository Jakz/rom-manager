package jack.rm.files;

import jack.rm.Settings;
import jack.rm.data.*;
import jack.rm.data.set.*;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.plugins.PluginRealType;
import jack.rm.plugins.folder.FolderPlugin;
import jack.rm.plugins.renamer.PatternSetPlugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;

public class Organizer
{	
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
	
	public static Set<Pattern> getPatterns(RomSet<?> set)
	{
	  Set<Pattern> patterns = new TreeSet<Pattern>();
	  
	  Set<PatternSetPlugin> plugins = set.getSettings().plugins.getPlugins(PluginRealType.PATTERN_SET);
	  plugins.forEach( p -> p.getPatterns().forEach(patterns::add) );
	  
	  return patterns;
	}
		
	public static String getCorrectName(Rom rom)
	{
		String temp = new String(Settings.current().renamingPattern);
		
		Set<Pattern> patterns = getPatterns(RomSet.current);
		
		for (Pattern p : patterns)
			temp = p.apply(temp, rom);
		
		return temp;
	}
	
	public static Path getCorrectFolder(Rom rom)
	{
	  Path base = Settings.current().romsPath;
	  
	  FolderPlugin organizer = Settings.current().getFolderOrganizer();
	  
	  if (organizer != null)
	    return base.resolve(organizer.getFolderForRom(rom));
	  else if (rom.entry != null)
	    return rom.entry.file().getParent();
	  else return base;
	}

	public static void organizeRomIfNeeded(Rom rom, boolean renamePhase, boolean movePhase)
	{
	  Settings settings = Settings.current();
	  OrganizerDetails details = settings.organizer;
	  
	  if (renamePhase && details.hasRenamePolicy() && !rom.hasCorrectName())
	    renameRom(rom);
	  
	  if (movePhase && settings.getFolderOrganizer() != null && !rom.hasCorrectFolder())
	    moveRom(rom);
	}
	
	public static void renameRom(Rom rom)
	{
    Path renameTo = rom.entry.file().getParent();
    
    if (rom.entry.type != RomType.BIN)
      renameTo = renameTo.resolve(Organizer.getCorrectName(rom).toString()+"."+rom.entry.type.ext);
    else
      renameTo = renameTo.resolve(Organizer.getCorrectName(rom).toString()+"."+RomSet.current.type.exts[0]);

    Path tmp = rom.entry.file();
  
    try
    {
      Files.move(tmp, renameTo);
      rom.status = RomStatus.FOUND;
      rom.entry = rom.entry.build(renameTo);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      // TODO: handle and write on log
    }
	}
	
	public static void moveRom(Rom rom)
	{
	  if (rom.status != RomStatus.NOT_FOUND)
    {     
      try
      {      
        Path finalPath = getCorrectFolder(rom);
  
        if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
        {
          Files.createDirectories(finalPath);
          Log.message(LogSource.ORGANIZER, LogTarget.none(), "Creating folder "+finalPath);
        }
        
        Path newFile = finalPath.resolve(rom.entry.file().getFileName());
                
        if (!newFile.equals(rom.entry.file()) && Files.exists(newFile))
        {
          
          Log.error(LogSource.ORGANIZER, LogTarget.rom(rom), "Cannot rename to "+newFile.toString()+", file exists");
        }
        else if (!newFile.equals(rom.entry.file()))
        {  
          Files.move(rom.entry.file(), newFile);
          rom.entry = rom.entry.build(newFile);
          Log.message(LogSource.ORGANIZER, LogTarget.rom(rom), "Moved rom to "+finalPath);
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
