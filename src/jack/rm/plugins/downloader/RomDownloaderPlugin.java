package jack.rm.plugins.downloader;

import java.net.URL;

import com.github.jakz.romlib.data.platforms.Platform;
import com.pixbits.lib.plugin.PluginType;

import jack.rm.data.rom.Rom;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class RomDownloaderPlugin extends ActualPlugin
{
  @Override public final PluginType<?> getPluginType() { return PluginRealType.ROM_DOWNLOADER; }
  
  public abstract boolean isPlatformSupported(Platform platform);
  public abstract URL getDownloadURL(Platform platform, Rom rom);
}
