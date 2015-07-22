package jack.rm.data.set;

import jack.rm.assets.Asset;
import jack.rm.data.*;
import jack.rm.data.console.System;
import jack.rm.data.parser.*;
import jack.rm.files.Organizer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.awt.Dimension;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class RomSetOfflineList extends RomSet<NumberedRom>
{
	private static class AssetDownloader implements jack.rm.assets.AssetManager
	{
	  final URL url;
	  private final DecimalFormat format;
	  AssetDownloader(URL url)
	  {
	    this.url = url;
	    format = new DecimalFormat();
	    format.applyPattern("0000");
	  }
	  
	  @Override public URL assetURL(Asset asset, Rom rom)
	  {
	    try
	    {
	      int first = (((((NumberedRom)rom).number-1)/500)*500) + 1;
	      int last = (((((NumberedRom)rom).number-1)/500+1)*500);
	      String partial = first+"-"+last+"/";
	      String suffix = asset == assets[0] ? "a.png" : "b.png";
	      return new URL(url, partial+rom.getAssetData(asset).getURLData());
	    }
	    catch (MalformedURLException e)
	    {
	      e.printStackTrace();
	      return null;
	    }
	  }
	  
	  private final static Asset[] assets = 
	  {
	     new Asset.Image(Paths.get("title"), new Dimension(480,320)),
	     new Asset.Image(Paths.get("gameplay"), new Dimension(480,320))
	  };
	  
	  @Override public Asset[] getSupportedAssets() { return assets; }

	}
		
	public RomSetOfflineList(System type, ProviderID provider, Dimension screenTitle, Dimension screenGame)
	{
		this(type,provider,screenTitle,screenGame,null);
	}
	
	public RomSetOfflineList(System type, ProviderID provider, Dimension screenTitle, Dimension screenGame, URL artDownloadURL)
	{
		super(type,provider,screenTitle,screenGame,artDownloadURL != null ? new AssetDownloader(artDownloadURL) : null);
	}
	
	@Override
  public String ident()
	{
		return provider.tag+"-"+system.tag+"-ol";
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
		loadDat(new OfflineListXMLParser(list, AssetDownloader.assets), datPath());
	}
}
