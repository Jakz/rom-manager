package jack.rm.plugins.providers.offlinelist;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.UnknownFormatConversionException;
import jack.rm.assets.Asset;
import jack.rm.data.console.GBA;
import jack.rm.data.console.NDS;
import jack.rm.data.console.System;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.RomSave;
import jack.rm.data.rom.RomSize;
import jack.rm.data.rom.Version;
import jack.rm.data.romset.Provider;
import jack.rm.data.romset.RomSet;
import jack.rm.files.parser.SaveParser;
import jack.rm.files.parser.XMLDatLoader;
import jack.rm.plugins.providers.*;

public class OfflineListProviderPlugin extends ProviderPlugin
{
  private final Provider PROVIDER = new OfflineListProvider();
  
  private final static Asset[] GBA_ASSETS =  
  {
    new Asset.Image(Paths.get("title"), new Dimension(480,320)),
    new Asset.Image(Paths.get("gameplay"), new Dimension(480,320))
  };
  
  private final static Asset[] NDS_ASSETS = 
  {
    new Asset.Image(Paths.get("title"), new Dimension(214,384)),
    new Asset.Image(Paths.get("gameplay"), new Dimension(256,384))
  };
  
  private final static Asset[] GB_ASSETS = 
  {
    new Asset.Image(Paths.get("title"), new Dimension(320,288)),
    new Asset.Image(Paths.get("gameplay"), new Dimension(320,288))
  };
  
  private final static Attribute[] GBA_ATTRIBUTES = 
  {
    RomAttribute.TITLE,
    RomAttribute.NUMBER,
    RomAttribute.PUBLISHER,
    RomAttribute.GROUP,
    RomAttribute.SIZE,
    RomAttribute.SAVE_TYPE,
    RomAttribute.LOCATION,
    RomAttribute.LANGUAGE,
    RomAttribute.CRC,
    RomAttribute.COMMENT
  };
  
  private final static Attribute[] GB_ATTRIBUTES = 
  {
    RomAttribute.TITLE,
    RomAttribute.NUMBER,
    RomAttribute.PUBLISHER,
    RomAttribute.GROUP,
    RomAttribute.SIZE,
    RomAttribute.LOCATION,
    RomAttribute.LANGUAGE,
    RomAttribute.CRC,
    RomAttribute.COMMENT
  };
  
  private final static Attribute[] NES_ATTRIBUTES = 
  {
    RomAttribute.TITLE,
    RomAttribute.NUMBER,
    RomAttribute.LOCATION,
    RomAttribute.LANGUAGE,
    RomAttribute.SIZE,
    RomAttribute.PUBLISHER,
    RomAttribute.SAVE_TYPE,
    RomAttribute.CRC,
    RomAttribute.COMMENT,
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
  
  private static class GBASaveParserOL implements SaveParser
  {
    public RomSave<?> parse(String string)
    {
      String[] tokens = string.split("_");
      
      if (tokens.length == 1 && tokens[0].compareToIgnoreCase(GBA.Save.Type.NONE.toString()) == 0)
        return new GBA.Save(GBA.Save.Type.NONE);
      else if (tokens.length >= 2)
      {
        if (tokens.length > 2)
          tokens[1] = tokens[2];
        
        for (GBA.Save.Type type : GBA.Save.Type.values())
        {
          if (tokens[0].toLowerCase().contains(type.toString().toLowerCase()))
          {
            Version[] versions = GBA.Save.valuesForType(type);
            
            for (Version version : versions)
            {
              if (tokens[1].contains(version.toString()))
                return new GBA.Save(type, version);
            }
            
            throw new UnknownFormatConversionException("Unable to parse GBA save: "+string);
            
          }
        }
      }
      
      return new GBA.Save(GBA.Save.Type.NONE);
    }
  }
  
  private static class GBASaveParserAS implements SaveParser
  {
    public RomSave<?> parse(String string)
    {
      if (string.toLowerCase().equals("none"))
        return new GBA.Save(GBA.Save.Type.NONE);
      else if (string.toLowerCase().equals("tbc"))
        return new GBA.Save(GBA.Save.Type.TBC);
      else if (string.equals("Eeprom - 4 kbit"))
        return new GBA.Save(GBA.Save.Type.EEPROM, GBA.Save.EEPROM.v122, 8192);
        
      if (string.toLowerCase().contains("sram"))
      {
        for (Version version :  GBA.Save.valuesForType(GBA.Save.Type.SRAM))
          if (string.contains(version.toString()))
            return new GBA.Save(GBA.Save.Type.SRAM, version, 32768);
      }
      else if (string.toLowerCase().contains("flash"))
      {

        for (Version version :  GBA.Save.valuesForType(GBA.Save.Type.FLASH))
          if (string.contains(version.toString()))
          {
            int size = (string.contains("512") ? 512 : 1024 ) * (int)RomSize.KBYTE / 8;
            return new GBA.Save(GBA.Save.Type.FLASH, version, size);
          }
      }
      else if (string.toLowerCase().contains("eeprom"))
      {
        for (Version version :  GBA.Save.valuesForType(GBA.Save.Type.EEPROM))
          if (string.contains(version.toString()))
          {
            return new GBA.Save(GBA.Save.Type.EEPROM, version, string.contains("64") ? 8192 : (8192/16));
          }
      }

      throw new UnknownFormatConversionException("Unable to parse GBA save: "+string);
    }
  }
  
  private static class NDSSaveParser implements SaveParser
  {
    public RomSave<?> parse(String string)
    {
      if (string.equals("TBC"))
        return new NDS.Save(NDS.Save.Type.TBC);
      else if (string.equals("None"))
        return new NDS.Save(NDS.Save.Type.NONE);
      
      String[] tokens = string.split(" - ");
      tokens = Arrays.stream(tokens).map(String::trim).map(String::toLowerCase).toArray(String[]::new);
      
      NDS.Save.Type type = null;
      long multiplier = 0;
      
      if (tokens[0].equals("flash"))
        type = NDS.Save.Type.FLASH;
      else if (tokens[0].equals("eeprom"))
        type = NDS.Save.Type.EEPROM;
      else
        throw new UnknownFormatConversionException("Unable to parse NDS save: "+string);
      
      if (tokens[1].endsWith("kbit"))
        multiplier = RomSize.KBIT;
      else if (tokens[1].endsWith("mbit"))
        multiplier = RomSize.MEGABIT;
      else
        throw new UnknownFormatConversionException("Unable to parse NDS save: "+string);

      String ntoken = tokens[1].substring(0, tokens[1].length() - 4).trim();
      
      multiplier *= Integer.valueOf(ntoken);
      
      return new NDS.Save(type, multiplier);
    }
  }
  
  @Override
  public RomSet buildRomSet(System system)
  {
    try
    {
    
    if (system == System.GBA)
    {
      /*RomSet romSet = new RomSet(
          system, 
          new OfflineListProvider(), 
          new OfflineListDatFormat(),
          GBA_ATTRIBUTES, 
          new AssetManager(GBA_ASSETS, new URL("http://offlinelistgba.free.fr/imgs/")), 
          new XMLDatLoader(new OfflineListXMLParser(new GBASaveParserOL()))
      );*/
      RomSet romSet = new RomSet(
          system, 
          new AdvanSceneProvider(), 
          new OfflineListDatFormat(),
          GBA_ATTRIBUTES, 
          new AssetManager(GBA_ASSETS, new URL("http://www.advanscene.com/offline/imgs/ADVANsCEne_GBA/")), 
          new XMLDatLoader(new OfflineListXMLParser(new GBASaveParserAS()))
      );
      return romSet;
    }
    else if (system == System.NDS)
    {
      RomSet romSet = new RomSet(
          system, 
          new AdvanSceneProvider(), 
          new OfflineListDatFormat(),
          GBA_ATTRIBUTES, 
          new AssetManager(NDS_ASSETS, new URL("http://www.advanscene.com/offline/imgs/ADVANsCEne_NDS/")), 
          new XMLDatLoader(new OfflineListXMLParser(new NDSSaveParser()))
      );
      return romSet;
    }
    else if (system == System.GBC)
    {
      RomSet romSet = new RomSet(
        system,
        new NoIntroProvider(),
        new OfflineListDatFormat(),
        GB_ATTRIBUTES,
        new AssetManager(GB_ASSETS, new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy%20Color/")),
        new XMLDatLoader(new OfflineListXMLParser(r -> null))
      );
      return romSet;
    }
    else if (system == System.GB)
    {
      RomSet romSet = new RomSet(
          system,
          new NoIntroProvider(),
          new OfflineListDatFormat(),
          GB_ATTRIBUTES,
          new AssetManager(GB_ASSETS, new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/")),
          new XMLDatLoader(new OfflineListXMLParser(r -> null))
        );
        return romSet;
    }
    else if (system == System.NES)
    {
      RomSet romSet = new RomSet(
        system,
        new NoIntroProvider(),
        new OfflineListDatFormat(),
        NES_ATTRIBUTES,
        new AssetManager(GB_ASSETS, new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20NES%20-%20Famicom/")),
        new XMLDatLoader(new OfflineListXMLParser(r -> null))
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
    return system == System.GBA || system == System.NDS || system == System.GB || system == System.GBC || system == System.NES;
  }

}
