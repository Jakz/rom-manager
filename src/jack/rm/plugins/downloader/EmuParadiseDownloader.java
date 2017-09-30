package jack.rm.plugins.downloader;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.platforms.Platforms;

import jack.rm.plugins.types.RomDownloaderPlugin;

public class EmuParadiseDownloader extends RomDownloaderPlugin
{
  private final Map<Platform, Integer> idents = new HashMap<>();
  private final String query1 = "http://www.emuparadise.me/roms/search.php?query=";
  private final String query2 = "&section=roms&sysid=";
  
  public EmuParadiseDownloader()
  {
    idents.put(Platforms.GBA, 31);
    idents.put(Platforms.GBC, 11);
    idents.put(Platforms.GB, 12);
    idents.put(Platforms.NES, 13);
    idents.put(Platforms.NDS, 32);
  }
  
  @Override public boolean isPlatformSupported(Platform platform)
  {
    return idents.containsKey(platform);
  }

  @Override public URL getDownloadURL(Platform platform, Game rom)
  {
    String name = rom.getTitle().replaceAll("\\W", " ").toLowerCase();
    name = name.replace(" ","%20");
    
    try {
      return new URL(query1+name+query2+idents.get(platform).toString());
    }
    catch (MalformedURLException e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
