package jack.rm.data.set;

import jack.rm.Main;
import jack.rm.Paths;
import jack.rm.data.Renamer;
import jack.rm.data.Rom;
import jack.rm.data.parser.*;

import java.awt.Dimension;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class RomSetOfflineList extends RomSet
{
	String artDownloadURL;
	
	public RomSetOfflineList(Console type, Provider provider, Dimension screenTitle, Dimension screenGame)
	{
		super(type,provider,screenTitle,screenGame);
	}
	
	public RomSetOfflineList(Console type, Provider provider, Dimension screenTitle, Dimension screenGame, String artDownloadURL)
	{
		this(type,provider,screenTitle,screenGame);
		this.artDownloadURL = artDownloadURL;
	}
	
	public String titleImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return artDownloadURL+partial+(rom.imageNumber)+"a.png";
	}
	
	public String gameImageURL(Rom rom)
	{
		String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		return artDownloadURL+partial+(rom.imageNumber)+"b.png";
	}
	
	public String titleImage(Rom rom)
	{
		return Paths.screensTitle()+Renamer.formatNumber(rom.imageNumber)+".png";
	}
	
	public String gameImage(Rom rom)
	{
		return Paths.screensGame()+Renamer.formatNumber(rom.imageNumber)+".png";
	}
	
	public String ident()
	{
		return provider.tag+"-"+type.tag+"-ol";
	}
	
	private void loadDat(DefaultHandler handler, String path)
	{
		try
		{
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(handler);
			
			reader.parse(path);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void load()
	{
		loadDat(new OfflineListXMLParser(Main.romList), datPath());
	}
	
	public abstract String downloadURL(Rom rom);
}
