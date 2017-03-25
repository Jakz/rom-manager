package jack.rm.plugins.scanners;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.pixbits.lib.io.archive.ArchiveFormat;
import com.pixbits.lib.io.archive.HandleSet;
import com.pixbits.lib.io.archive.Scanner;
import com.pixbits.lib.io.archive.ScannerOptions;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.plugin.ExposedParameter;
import com.pixbits.lib.plugin.PluginInfo;
import com.pixbits.lib.plugin.PluginVersion;

public class EmbeddedScanner extends ScannerPlugin
{
  @ExposedParameter(name="Scan subfolders", description="whether the plugin should scan subfolders") boolean scanSubfolders = true;
  @ExposedParameter(name="Scan binaries", description="whether the plugin allows binaries") boolean scanBinaries = true;
  @ExposedParameter(name="Scan archives", description="whether the plugin allows archives") boolean scanArchives = true;
  @ExposedParameter(name="Scan nested archives", description="whether the plugin allows nested archives") boolean scanNestedArchives = true;
  
  
  @Override public String[] getHandledExtensions() { return ArchiveFormat.readableExtensions; }

  @Override
  public PluginInfo getInfo()
  { 
    return new PluginInfo("Embedded Scanner", new PluginVersion(1,0), "Jack",
        "This plugin provides the embedded scanner which is able to scan binary, archives and nested archives.");
  }
  
  @Override
  public List<VerifierEntry> scanFile(Path path) throws IOException
  {
    ScannerOptions options = new ScannerOptions();
    options.assumeCRCisCorrect = true; // TODO: depends on specs of rom set
    options.multithreaded = false;
    options.scanSubfolders = scanSubfolders;
    options.scanBinaries = scanBinaries;
    options.scanArchives = scanArchives;
    options.scanNestedArchives = scanNestedArchives;
    
    Scanner scanner = new Scanner(options);
    
    return scanner.scanSinglePath(path);
  }

}
