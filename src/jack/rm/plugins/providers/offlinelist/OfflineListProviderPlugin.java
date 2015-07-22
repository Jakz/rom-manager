package jack.rm.plugins.providers.offlinelist;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import jack.rm.assets.Asset;
import jack.rm.data.Rom;
import jack.rm.data.console.System;
import jack.rm.data.parser.XMLDatLoader;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.set.Provider;
import jack.rm.data.set.RomSet;
import jack.rm.plugins.providers.ProviderPlugin;

public class OfflineListProviderPlugin extends ProviderPlugin
{
  private final Provider PROVIDER = new OfflineListProvider();
  
  private final static Asset[] GBA_ASSETS =  
  {
    new Asset.Image(Paths.get("title"), new Dimension(480,320)),
    new Asset.Image(Paths.get("gameplay"), new Dimension(480,320))
  };
  
  private final static RomAttribute[] GBA_ATTRIBUTES = 
  {
    RomAttribute.TITLE,
    RomAttribute.NUMBER,
    RomAttribute.PUBLISHER,
    RomAttribute.GROUP,
    RomAttribute.SIZE,
    RomAttribute.LOCATION,
    RomAttribute.SERIAL,
    RomAttribute.LANGUAGE,
    RomAttribute.CRC,
    RomAttribute.COMMENT
  };
  
  private static class AssetManager implements jack.rm.assets.AssetManager
  {
    final URL url;
    private final DecimalFormat format;
    private final Asset[] assets;
    
    AssetManager(Asset[] assets, URL url)
    {
      this.url = url;
      this.assets = assets;
      format = new DecimalFormat();
      format.applyPattern("0000");
    }
    
    @Override public URL assetURL(Asset asset, Rom rom)
    {
      try
      {
        int number = rom.getAttribute(RomAttribute.NUMBER);
        
        int first = (((number-1)/500)*500) + 1;
        int last = (((number-1)/500+1)*500);
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

    @Override public Asset[] getSupportedAssets() { return assets; }
  }
  
  @Override
  public RomSet buildRomSet(System system)
  {
    try
    {
    
    if (system == System.GBA)
    {
      RomSet romSet = new RomSet(
          system, 
          PROVIDER, 
          () -> "ol",
          GBA_ATTRIBUTES, 
          new OfflineListProviderPlugin.AssetManager(GBA_ASSETS, new URL("http://offlinelistgba.free.fr/imgs/")), 
          new XMLDatLoader(new OfflineListXMLParser())
      );
      
      return romSet;
    }
    
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
    }
    
    // 3DS 268,240 http://www.advanscene.com/offline/imgs/ADVANsCEne_3DS/
    // GB 320,288 http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/
    // GBC 320,288 http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy%20Color/
    // NDS 214,384 256,384 http://www.retrocovers.com/offline/imgs/ADVANsCEne_NDS/
    // NES 256,240 http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20NES%20-%20Famicom/
    // WS 448,448 http://nointro.free.fr/imgs/Official%20No-Intro%20Bandai%20WonderSwan/
    
    return null;
  }

  @Override public boolean isSystemSupported(System system)
  {
    return system == System.GBA;
  }

}
