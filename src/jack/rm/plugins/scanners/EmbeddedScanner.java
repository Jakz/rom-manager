package jack.rm.plugins.scanners;

import java.nio.file.Path;

import jack.rm.data.romset.RomHashFinder;
import jack.rm.files.ScanResult;

public class EmbeddedScanner extends ScannerPlugin
{

  @Override protected int getPriority() { return Integer.MAX_VALUE; }
  @Override public String[] getHandledExtensions() { return null; }

  @Override
  public ScanResult scanRom(RomHashFinder finder, Path file)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
