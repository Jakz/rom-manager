package jack.rm.data.set;

import jack.rm.Paths;
import jack.rm.Settings;
import jack.rm.data.*;

import java.awt.Dimension;

import jack.rm.data.parser.*;
import org.xml.sax.helpers.DefaultHandler;

public abstract class RomSet
{
	public static RomSet current = null;


	public final Console type;
	public final String romPath;
	public final Provider provider;
	
	public final Dimension screenTitle;
	public final Dimension screenGame;
	
	RomSet(Console type, Provider provider, String romPath, Dimension screenTitle, Dimension screenGame)
	{
		this.type = type;
		this.romPath = romPath;
		this.provider = provider;
		this.screenTitle = screenTitle;
		this.screenGame = screenGame;
		
		Settings.get(this);
	}
	
	public abstract String titleImageURL(Rom rom);
	public abstract String gameImageURL(Rom rom);
	public abstract String titleImage(Rom rom);
	public abstract String gameImage(Rom rom);
	public abstract String downloadURL(Rom rom);
	
	public DefaultHandler buildDatLoader(RomList list)
	{
		return new OfflineListXMLParser(list);
	}
	
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
		return Paths.dats+ident()+".xml";
	}

}
