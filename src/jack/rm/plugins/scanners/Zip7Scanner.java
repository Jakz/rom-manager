package jack.rm.plugins.scanners;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
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
import jack.rm.files.romhandles.Zip7Handle;
import jack.rm.log.Log;
import jack.rm.log.LogSource;
import jack.rm.log.LogTarget;
import jack.rm.log.LogType;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class Zip7Scanner extends ScannerPlugin
{
  final static String[] extensions = new String[] { "7z" };
  
  @Override protected int getPriority() { return 0; }
  
  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("7zip Scanner", new PluginVersion(1,0), "Jack",
        "This plugin scans 7zip archives.");
  }
  
  @Override public String[] getHandledExtensions() { return extensions; }

  @Override public ScanResult scanRom(RomHashFinder finder, Path path)
  {
    try (RandomAccessFileInStream file = new RandomAccessFileInStream(new RandomAccessFile(path.toFile(), "r")))
    {
      try (IInArchive archive = SevenZip.openInArchive(null, file))
      {
        int filesCount = archive.getNumberOfItems();
        
        for (int i = 0; i < filesCount; ++i)
        {
          long crc = Integer.toUnsignedLong((Integer)archive.getProperty(i, PropID.CRC));
          Rom rom = finder.getByCRC32(crc);
          
          if (rom != null)
            return new ScanResult(rom, new Zip7Handle(path, (String)archive.getProperty(i, PropID.PATH), i));
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    
    return null;
  }
}
