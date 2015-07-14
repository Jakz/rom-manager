package jack.rm.plugins.folder;

import java.nio.file.Paths;
import java.nio.file.Path;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import jack.rm.data.Rom;

public class AlphabeticalOrganizer extends FolderPlugin
{
  @Override
  public Path getFolderForRom(Rom rom)
  {
    String normalizedName = Normalizer.normalize(rom.title, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    return Paths.get(normalizedName.toUpperCase().charAt(0)+java.io.File.separator);
  }
  
  @Override
  public String getDescription()
  {
    return "This plugin organizes ROMs by folders named with the first letter of the rom title";
  }
}
