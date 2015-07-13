package jack.rm.files;

import jack.rm.plugin.folder.*;

public class OrganizerDetails
{
  public RenamePolicy renamePolicy;
  
  public boolean moveUnknown;  
  public boolean deleteEmptyFolders;
  
  public boolean shouldCleanup()
  {
    return moveUnknown || deleteEmptyFolders;
  }
  
  public OrganizerDetails()
  {
    renamePolicy = RenamePolicy.FILES;
    
    moveUnknown = true;
    deleteEmptyFolders = true;
  }
  
  public RenamePolicy getRenamePolicy() { return renamePolicy; }
  
  public boolean hasRenamePolicy() { return renamePolicy != RenamePolicy.NONE; }
  public boolean shouldOrganize() { return/* hasFolderOrganizer() ||*/ hasRenamePolicy(); }

  public boolean shouldMoveUnknownFiled() { return moveUnknown; }
  public boolean shouldDeleteEmptyFolders() { return deleteEmptyFolders; }
}
