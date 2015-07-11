package jack.rm.files;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.*;
import jack.rm.data.set.*;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	
	public static boolean isCorrectlyNamed(String name, Rom rom)
	{
		return !Settings.current().useRenamer || name.equals(getCorrectName(rom));
	}
		
	public static String getCorrectName(Rom rom)
	{
		String temp = new String(Settings.current().renamingPattern);
		
		for (Pattern p : patterns)
			temp = p.apply(temp, rom);
		
		return temp;
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
		public String apply(String name, Rom rom) { return name.replace(code,format.format(rom.number)); }
	}
	
	static class TitlePattern extends Pattern {
		TitlePattern() { super("%t", "Game title"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.title); }
	}
	
	static class PublisherPattern extends Pattern {
		PublisherPattern() { super("%c", "Publisher"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.publisher); }
	}
	
	static class GroupPattern extends Pattern {
		GroupPattern() { super("%g", "Releaser group"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.group); }
	}
	
	static class MegabyteSizePattern extends Pattern {
		MegabyteSizePattern() { super("%s", "Size of the game dump in megabytes"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.size.mbytesAsString()); }
	}
	
	static class MegabitSizePattern extends Pattern {
		MegabitSizePattern() { super("%S", "Size of the game dump in megabits"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.size.bitesAsStringShort()); }
	}
	
	static class FullLocationPattern extends Pattern {
		FullLocationPattern() { super("%L", "Full location name"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.location.fullName); }
	}
	
	static class ShortLocationPattern extends Pattern {
		ShortLocationPattern() { super("%a", "Short location name"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.location.shortName); }
	}
	
	static class TinyLocationPattern extends Pattern {
		TinyLocationPattern() { super("%l", "Tiny location name"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.location.tinyName); }
	}
	
	static class ShortLanguagePattern extends Pattern {
		ShortLanguagePattern() { super("%i", "Short language"); }
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
	
	public static void renameRom(Rom rom)
	{
    String renameTo = rom.file.file().getParent()+File.separator+Organizer.getCorrectName(rom)+".";
    
    if (rom.file.type != RomType.BIN)
      renameTo += rom.file.type.ext;
    else
      renameTo += RomSet.current.type.exts[0];
            
    File tmp = rom.file.file();
    
    File newF = new File(renameTo);
    
    try
    {
      Files.move(tmp.toPath(), Paths.get(renameTo));
      rom.status = RomStatus.FOUND;
      rom.file = rom.file.build(newF);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      // TODO: handle and write on log
    }
	}
	
	public static void organizeRom(Rom rom, int folderSize)
	{
	  if (rom.status != RomStatus.NOT_FOUND)
    {     
      try
      {      
  	    int which = (rom.number - 1) / folderSize;
        
        String first = Organizer.formatNumber(folderSize*which+1);
        String last = Organizer.formatNumber(folderSize*(which+1));
        
        Path finalPath = RomSet.current.romPath().resolve(first+"-"+last+File.separator);
  
        if (!Files.exists(finalPath) || !Files.isDirectory(finalPath))
        {
          Files.createDirectories(finalPath);
          Log.log(LogType.MESSAGE, LogSource.ORGANIZER, "Creating folder "+finalPath);
        }
        
        Path newFile = finalPath.resolve(rom.file.file().getName());
                
        if (Files.exists(newFile))
        {
          Log.log(LogType.ERROR, LogSource.ORGANIZER, LogTarget.rom(rom), "Cannot rename to "+newFile.toString()+", file exists");
        }
        else if (!newFile.equals(rom.file.file()))
        {  
          Files.move(rom.file.file().toPath(), newFile);
          rom.file = rom.file.build(newFile.toFile());
          Log.log(LogType.MESSAGE, LogSource.ORGANIZER, LogTarget.rom(rom), "Moved rom to "+finalPath);
        }    
      }
      catch (Exception e)
      {
        //TODO: handle and log
        e.printStackTrace();
      }   
    } 
	}
	
	public static void moveUnknownFiles(RomList list)
	{
	  try
	  {
	    Path path = Settings.current().unknownPath;
	  
	    if (!Files.exists(path) || !Files.isDirectory(path))
	      Files.createDirectory(path);

	    Set<File> existing = list.stream()
	      .filter( r -> r.status != RomStatus.NOT_FOUND )
	      .map( r -> r.file.file())
	      .collect(Collectors.toSet());

	    Set<File> total = new FolderScanner(new AllFileFilter()).scan(Settings.current().romsPath.toFile());
	    
	    total.removeAll(existing);
	    
	    total.stream()
	      .filter( f -> !f.getParentFile().equals(path.toFile()) )
	      .forEach( f -> {
	        Path dest = path.resolve(f.getName());
	        int i = 1;
	        
	        while (Files.exists(dest))
	          dest = path.resolve(f.getName()+(i++));

	        try { Files.move(f.toPath(), dest); }
	        catch (IOException e) { e.printStackTrace(); /* TODO: log */ }
   
	      });
	    
	    
	  }
	  catch (IOException e)
	  {
	    e.printStackTrace();
	    // TODO: log
	  }
	  
	}
}
