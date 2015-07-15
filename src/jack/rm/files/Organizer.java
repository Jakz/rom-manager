package jack.rm.files;

import jack.rm.Settings;
import jack.rm.data.*;
import jack.rm.data.set.*;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;
import jack.rm.plugins.folder.FolderPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Organizer
{
	public final static ArrayList<Pattern> patterns = new ArrayList<Pattern>();
	
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
		
	public static String getCorrectName(Rom rom)
	{
		String temp = new String(Settings.current().renamingPattern);
		
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
	
	public abstract static class Pattern {
		public final String code, desc;
		public Pattern(String code, String desc) { 
			this.code = code;
			this.desc = desc;
			patterns.add(this);
		}
		
		public abstract String apply(String name, Rom rom);
	}
	
	static
	{
		new NumberPattern();
		new TitlePattern();
		new PublisherPattern();
		new MegabyteSizePattern();
		new MegabitSizePattern();
		new FullLocationPattern();
		new ShortLocationPattern();
		new TinyLocationPattern();
		new ShortLanguagePattern();
		new GroupPattern();
	}
	
	static class NumberPattern extends Pattern {
		NumberPattern() { super("%n", "Release number in format 1234"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,format.format(((NumberedRom)rom).number)); }
	}
	
	static class TitlePattern extends Pattern {
		TitlePattern() { super("%t", "Game title"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.title); }
	}
	
	static class PublisherPattern extends Pattern {
		PublisherPattern() { super("%c", "Publisher"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.publisher); }
	}
	
	static class GroupPattern extends Pattern {
		GroupPattern() { super("%g", "Releaser group"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.group); }
	}
	
	static class MegabyteSizePattern extends Pattern {
		MegabyteSizePattern() { super("%s", "Size of the game dump in bytes (long)"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.size.toString(RomSize.PrintStyle.LONG, RomSize.PrintUnit.BYTES)); }
	}
	
	static class MegabitSizePattern extends Pattern {
		MegabitSizePattern() { super("%S", "Size of the game dump in bits (short)"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.size.toString(RomSize.PrintStyle.SHORT, RomSize.PrintUnit.BITS)); }
	}
	
	static class FullLocationPattern extends Pattern {
		FullLocationPattern() { super("%L", "Full location name"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.location.fullName); }
	}
	
	static class ShortLocationPattern extends Pattern {
		ShortLocationPattern() { super("%a", "Short location name"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.location.shortName); }
	}
	
	static class TinyLocationPattern extends Pattern {
		TinyLocationPattern() { super("%l", "Tiny location name"); }
		@Override
    public String apply(String name, Rom rom) { return name.replace(code,rom.location.tinyName); }
	}
	
	static class ShortLanguagePattern extends Pattern {
		ShortLanguagePattern() { super("%i", "Short language"); }
		@Override
    public String apply(String name, Rom rom) {
			int c = 0;
			Language l = null;
			
			for (Language l2 : Language.values())
				if ((rom.languages & l2.code) != 0)
				{
					++c;
					l = l2;
				}
			
			if (c == 1)
				return name.replace(code,l.iso639_1);
			else 
				return name.replace(code,"M"+c);
		}
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
