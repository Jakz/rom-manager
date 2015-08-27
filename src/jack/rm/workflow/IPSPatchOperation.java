package jack.rm.workflow;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import jack.rm.data.attachment.Attachment;
import jack.rm.data.attachment.AttachmentType;
import jack.rm.data.rom.Rom;
import jack.rm.files.IPSPatch;

public class IPSPatchOperation extends RomOperation
{
  Map<Rom, IPSPatch> patches;
  boolean automaticPatching;
  
  public IPSPatchOperation()
  {
    patches = new HashMap<>();
  }
  
  public void addPatch(Rom rom, Path patchFile)
  {
    try
    {
      IPSPatch patch = new IPSPatch(patchFile);
      patches.put(rom, patch);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public void toggleAutomaticPatching(boolean automaticPatching)
  {
    this.automaticPatching = automaticPatching;
  }
  
  public String getName() { return "IPS Patcher"; }
  public String getDescription() { return "This operation applies an IPS patch to a ROM"; }
  
  protected RomHandle doApply(RomHandle handle) throws Exception
  {
    IPSPatch patch = patches.get(handle.getRom());
    
    if (patch == null && automaticPatching)
    {
      Optional<Attachment> attachment = handle.getRom().getAttachments().stream().filter( a -> a.getType() == AttachmentType.IPS_PATCH).findFirst();
      
      if (attachment.isPresent())
      {
        patch = new IPSPatch(attachment.get().getPath());
        System.out.println("Automatic patch on "+handle.getRom().getTitle());
      }
      
    }
    
    if (patch != null)
      patch.apply(handle.getBuffer());
    
    return handle;
  }
}
