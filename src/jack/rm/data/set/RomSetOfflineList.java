package jack.rm.data.set;

import jack.rm.Settings;
import jack.rm.data.*;
import jack.rm.data.console.System;
import jack.rm.data.parser.*;
import jack.rm.files.Organizer;
import jack.rm.net.AssetDownloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.awt.Dimension;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class RomSetOfflineList extends RomSet<NumberedRom>
{
	class AssetDownloader implements jack.rm.net.AssetDownloader
	{
	  final URL url;
	  AssetDownloader(URL url) { this.url = url; }
	  
	  @Override public URL assetURL(Asset asset, Rom rom)
	  {
	    try
	    {
	      int first = (((((NumberedRom)rom).number-1)/500)*500) + 1;
	      int last = (((((NumberedRom)rom).number-1)/500+1)*500);
	      String partial = first+"-"+last+"/";
	      String suffix = asset == Asset.SCREEN_TITLE ? "a.png" : "b.png";
	      return new URL(artDownloadURL, partial+(rom.imageNumber)+suffix);
	    }
	    catch (MalformedURLException e)
	    {
	      e.printStackTrace();
	      return null;
	    }
	  }
	}
  
  URL artDownloadURL;
	
	
	
	private final AssetDownloader assetDownloader;
	
	public RomSetOfflineList(System type, ProviderID provider, Dimension screenTitle, Dimension screenGame)
	{
		this(type,provider,screenTitle,screenGame,null);
	}
	
	public RomSetOfflineList(System type, ProviderID provider, Dimension screenTitle, Dimension screenGame, URL artDownloadURL)
	{
		super(type,provider,screenTitle,screenGame);
		
    if (artDownloadURL != null)
      assetDownloader = new AssetDownloader(artDownloadURL);
    else
      assetDownloader = null;
	}
	
	private final Asset[] assets = new Asset[] { Asset.SCREEN_TITLE, Asset.SCREEN_GAMEPLAY };
	@Override public Asset[] getSupportedAssets() { return assets; }
	@Override public AssetDownloader getAssetDownloader() { return assetDownloader; }

	@Override
  public Path assetPath(Asset asset, Rom rom)
	{
		return Settings.getAssetPath(asset).resolve(Organizer.formatNumber(rom.imageNumber)+".png");
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
		loadDat(new OfflineListXMLParser(list), datPath());
	}
	
	@Override
  public abstract String downloadURL(Rom rom);
}
