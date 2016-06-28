package jack.rm.files.romhandles;

import java.nio.file.Path;

public abstract class ArchiveHandle extends RomPath
{
  public final Path file;

  protected ArchiveHandle(Type type, Path file)
  {
    super(type);
    this.file = file;
  }

  @Override public boolean isArchive() { return true; }
  
  public abstract boolean renameInternalFile(String newName);

}