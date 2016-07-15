package jack.rm.plugins.scanners;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;

import com.pixbits.plugin.PluginInfo;
import com.pixbits.plugin.PluginVersion;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomHashFinder;
import jack.rm.files.ScanResult;
import jack.rm.files.romhandles.RomPath;
import jack.rm.files.romhandles.Zip7MultiHandle;
import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.PropID;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

public class Zip7Scanner extends ScannerPlugin
{
  final static String[] extensions = new String[] { "7z", "rar" };
  
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
          {
            if (archive.getArchiveFormat() == ArchiveFormat.SEVEN_ZIP)
              return new ScanResult(rom, new Zip7MultiHandle(RomPath.Type._7ZIP, path, (String)archive.getProperty(i, PropID.PATH), i));
            else if (archive.getArchiveFormat() == ArchiveFormat.RAR)
              return new ScanResult(rom, new Zip7MultiHandle(RomPath.Type.RAR, path, (String)archive.getProperty(i, PropID.PATH), i));

          }
            
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
