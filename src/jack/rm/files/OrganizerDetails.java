package jack.rm.files;

import java.nio.file.Path;

import jack.rm.plugin.folder.*;

public class OrganizerDetails
{
  public RenamePolicy renamePolicy;
  public FolderPlugin folderPlugin;
  
  public boolean moveUnknown;  
  public boolean deleteEmptyFolders;
  
  public boolean shouldCleanup()
  {
    return moveUnknown || deleteEmptyFolders;
  }
  
  public OrganizerDetails()
  {
    renamePolicy = RenamePolicy.FILES;
    folderPlugin = new NumericalOrganizer();
    
    moveUnknown = true;
    deleteEmptyFolders = true;
  }
  
  public RenamePolicy getRenamePolicy() { return renamePolicy; }
  public FolderPlugin getFolderPolicy() { return folderPlugin; }
  
  public boolean hasFolderOrganizer() { return folderPlugin != null;}
  public boolean hasRenamePolicy() { return renamePolicy != RenamePolicy.NONE; }
  public boolean shouldOrganize() { return hasFolderOrganizer() || hasRenamePolicy(); }

  public boolean shouldMoveUnknownFiled() { return moveUnknown; }
  public boolean shouldDeleteEmptyFolders() { return deleteEmptyFolders; }
}
