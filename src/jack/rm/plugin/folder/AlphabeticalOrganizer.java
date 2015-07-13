package jack.rm.plugin.folder;

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
}
