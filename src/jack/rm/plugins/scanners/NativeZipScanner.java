package jack.rm.plugins.scanners;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomHashFinder;
import jack.rm.files.ScanResult;
import jack.rm.files.romhandles.RomPath;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;

public class NativeZipScanner extends ScannerPlugin
{
  final static PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{zip}");
  
  @Override protected int getPriority() { return 0; }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Native Zip Scanner", new PluginVersion(1,0), "Jack",
        "This plugin scans zip archives.");
  }
  
  @Override public PathMatcher getPathMatcher() { return matcher; }

  @Override public ScanResult scanRom(RomHashFinder finder, Path file)
  {
    try (ZipFile zip = new ZipFile(file.toFile()))
    {          
      Enumeration<? extends ZipEntry> enu = zip.entries();
      
      while (enu.hasMoreElements())
      {
        ZipEntry entry = enu.nextElement();
        long curCrc = entry.getCrc();
        
        Rom rom = finder.getByCRC32(curCrc);
        
        if (rom != null)
          return new ScanResult(rom, RomPath.build(RomPath.Type.ZIP, file, entry.getName()));
      }
    }
    catch (Exception e)
    {
      Log.log(LogType.ERROR, LogSource.SCANNER, LogTarget.file(file), "Zipped file is corrupt, skipping");
    }
    
    return null;
  }
}
