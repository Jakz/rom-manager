package jack.rm.plugins.scanners;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.pixbits.lib.io.archive.VerifierEntry;

import jack.rm.plugins.types.ScannerPlugin;

public class RetrozipScanner extends ScannerPlugin
{
  private static String[] extensions = { "box" };
  
  
  @Override
  public String[] getHandledExtensions() { return extensions; }

  @Override
  public List<VerifierEntry> scanFile(Path path) throws IOException
  {
    // TODO Auto-generated method stub
    return null;
  }

}
