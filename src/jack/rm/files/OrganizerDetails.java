package jack.rm.files;

public class OrganizerDetails
{
  public RenamePolicy renamePolicy;
  
  public OrganizerDetails()
  {
    renamePolicy = RenamePolicy.FILES;
  }
  
  public RenamePolicy getRenamePolicy() { return renamePolicy; }
  
  public boolean hasRenamePolicy() { return renamePolicy != RenamePolicy.NONE; }
  public boolean shouldOrganize() { return/* hasFolderOrganizer() ||*/ hasRenamePolicy(); }
}
