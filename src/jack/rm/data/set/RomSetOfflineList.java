package jack.rm.data.set;

import jack.rm.Main;
import jack.rm.Settings;
import jack.rm.data.Rom;
import jack.rm.data.parser.*;
import jack.rm.files.Organizer;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.awt.Dimension;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class RomSetOfflineList extends RomSet
{
	URL artDownloadURL;
	
	public RomSetOfflineList(Console type, Provider provider, Dimension screenTitle, Dimension screenGame)
	{
		super(type,provider,screenTitle,screenGame);
	}
	
	public RomSetOfflineList(Console type, Provider provider, Dimension screenTitle, Dimension screenGame, URL artDownloadURL)
	{
		this(type,provider,screenTitle,screenGame);
		this.artDownloadURL = artDownloadURL;
	}
	
	@Override
  public URL titleImageURL(Rom rom)
	{
		try
		{
		  String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		  return new URL(artDownloadURL, partial+(rom.imageNumber)+"a.png");
		}
		catch (MalformedURLException e)
		{
		  e.printStackTrace();
		  return null;
		}
	}
	
	@Override
  public URL gameImageURL(Rom rom)
	{
    try
    {
      String partial = ((((rom.number-1)/500)*500)+1)+"-"+((((rom.number-1)/500+1)*500))+"/";
		  return new URL(artDownloadURL, partial+(rom.imageNumber)+"b.png");
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
      return null;
    }
	}
	
	@Override
  public Path titleImage(Rom rom)
	{
		return Settings.screensTitle().resolve(Organizer.formatNumber(rom.imageNumber)+".png");
	}
	
	@Override
  public Path gameImage(Rom rom)
	{
		return Settings.screensGame().resolve(Organizer.formatNumber(rom.imageNumber)+".png");
	}
	
	@Override
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
	
	@Override
  public void load()
	{
		loadDat(new OfflineListXMLParser(Main.romList), datPath());
	}
	
	@Override
  public abstract String downloadURL(Rom rom);
}
