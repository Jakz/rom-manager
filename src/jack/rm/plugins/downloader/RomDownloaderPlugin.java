package jack.rm.plugins.downloader;

import java.net.URL;

import com.pixbits.lib.plugin.PluginType;

import jack.rm.data.console.System;
import jack.rm.data.rom.Rom;
import jack.rm.plugins.ActualPlugin;
import jack.rm.plugins.PluginRealType;

public abstract class RomDownloaderPlugin extends ActualPlugin
{
  @Override public final PluginType<?> getPluginType() { return PluginRealType.ROM_DOWNLOADER; }
  
  public abstract boolean isSystemSupported(System system);
  public abstract URL getDownloadURL(System system, Rom rom);
}
