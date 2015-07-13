package jack.rm.files;

import java.nio.file.Path;

public class OrganizerDetails
{
  private RenamePolicy renamePolicy;
  private FolderPolicy folderPolicy;

  private boolean moveUnknown;  
  private boolean deleteEmptyFolders;
  
  public boolean shouldCleanup()
  {
    return moveUnknown || deleteEmptyFolders;
  }
  
  OrganizerDetails()
  {
    renamePolicy = RenamePolicy.FILES;
    folderPolicy = FolderPolicy.ROM_NUMBER;
    
    moveUnknown = true;
    deleteEmptyFolders = true;
  }
  
  public RenamePolicy getRenamePolicy() { return renamePolicy; }
  public FolderPolicy getFolderPolicy() { return folderPolicy; }
  
  public boolean hasFolderPolicy() { return folderPolicy != FolderPolicy.NONE;}
  public boolean hasRenamePolicy() { return renamePolicy != RenamePolicy.NONE; }
  public boolean shouldOrganize() { return hasFolderPolicy() || hasRenamePolicy(); }

  public boolean shouldMoveUnknownFiled() { return moveUnknown; }
  public boolean shouldDeleteEmptyFolders() { return deleteEmptyFolders; }
}
