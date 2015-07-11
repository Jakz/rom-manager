package jack.rm.data.set;

import jack.rm.Settings;
import jack.rm.data.*;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.net.URL;
import java.util.*;
import java.util.stream.*;
import java.awt.Dimension;

public abstract class RomSet
{
	public static RomSet current = null;
	
	public final Console type;
	public final Provider provider;
	
	public final Dimension screenTitle;
	public final Dimension screenGame;
	
	RomSet(Console type, Provider provider, Dimension screenTitle, Dimension screenGame)
	{
		this.type = type;
		this.provider = provider;
		this.screenTitle = screenTitle;
		this.screenGame = screenGame;
		
		Settings.get(this);
	}
	
	public abstract URL titleImageURL(Rom rom);
	public abstract URL gameImageURL(Rom rom);
	public abstract Path titleImage(Rom rom);
	public abstract Path gameImage(Rom rom);
	public abstract String downloadURL(Rom rom);
	
	public abstract void load();
	
	@Override
  public String toString()
	{
		return type.name+" ("+provider.name+")";
	}
	
	public String ident()
	{
		return provider.tag+"-"+type.tag;
	}
	
	public String datPath()
	{
		return "dat/"+ident()+".xml";
	}

	public boolean hasGameArt()
	{
		return screenGame != null;
	}
	
	public boolean hasTitleArt()
	{
		return screenTitle != null;
	}
	
	public Path romPath()
	{
		return Settings.get(this).romsPath;
	}
	
	public PathMatcher getFileMatcher()
	{
	  Stream<String> stream = Arrays.stream(type.exts);
	  
	  if (type.acceptsArchives)
	    stream = Stream.concat(stream, Arrays.stream(new String[]{"zip"}));
	  
	  String pattern = stream.collect(Collectors.joining(",", "glob:*.{", "}"));
	  	  
	  return FileSystems.getDefault().getPathMatcher(pattern);
	}
}
