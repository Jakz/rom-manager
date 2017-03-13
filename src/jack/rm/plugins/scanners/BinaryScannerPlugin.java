package jack.rm.plugins.scanners;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomHashFinder;
import jack.rm.files.ScanResult;
import jack.rm.files.romhandles.RomPath;

public class BinaryScannerPlugin extends ScannerPlugin
{  
  @Override protected int getPriority() { return 1; }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Binary Scanner", new PluginVersion(1,0), "Jack",
        "This plugin scans raw binary files.");
  }
  
  @Override
  public String[] getHandledExtensions() { return null; }
  
  private static long computeCRC(Path file)
  {
    try (CheckedInputStream cis = new CheckedInputStream(new BufferedInputStream(Files.newInputStream(file)), new CRC32()))
    {
      byte[] buf = new byte[8192];
      
      while (cis.read(buf) >= 0);
      
      long crc = cis.getChecksum().getValue();
      
      return crc;
    }
    catch (ClosedByInterruptException e)
    {
      // thrown when cancelling a BackgroundOperation with the stream opened
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    
    return -1;
  }

  @Override public ScanResult scanRom(RomHashFinder finder, Path file)
  {
    try
    {
      long crc = FileUtils.calculateCRCFast(file);
      Rom rom = finder.getByCRC32(crc);
      return rom != null ? new ScanResult(rom, RomPath.build(RomPath.Type.BIN, file)) : null;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
}
