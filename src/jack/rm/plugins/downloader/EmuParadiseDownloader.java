package jack.rm.plugins.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jack.rm.data.console.System;
import jack.rm.data.Rom;

public class EmuParadiseDownloader extends RomDownloaderPlugin
{
  private final Map<System, Integer> idents = new HashMap<>();
  private final String query1 = "http://www.emuparadise.me/roms/search.php?query=";
  private final String query2 = "&section=roms&sysid=";
  
  EmuParadiseDownloader()
  {
    idents.put(System.GBA, 31);
    idents.put(System.GBC, 11);
    idents.put(System.GB, 12);
    idents.put(System.NES, 13);
    idents.put(System.NDS, 32);
  }
  
  @Override public boolean isSystemSupported(System system)
  {
    return idents.containsKey(system);
  }

  @Override public URL getDownloadURL(System system, Rom rom)
  {
    String name = rom.getTitle().replaceAll("\\W", " ").toLowerCase();
    name = name.replace(" ","%20");
    
    try {
      return new URL(query1+name+query2+idents.get(system).toString());
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
