package jack.rm.workflow;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.jakz.romlib.data.attachments.Attachment;
import com.github.jakz.romlib.data.attachments.AttachmentType;
import com.github.jakz.romlib.data.game.Game;

import jack.rm.files.IPSPatch;

public class IPSPatchOperation extends RomOperation
{
  Map<Game, IPSPatch> patches;
  boolean automaticPatching;
  
  public IPSPatchOperation()
  {
    patches = new HashMap<>();
  }
  
  public void addPatch(Game rom, Path patchFile)
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
  
  protected GameEntry doApply(GameEntry handle) throws Exception
  {
    IPSPatch patch = patches.get(handle.getGame());
    
    if (patch == null && automaticPatching)
    {
      Optional<Attachment> attachment = handle.getGame().getAttachments().stream().filter( a -> a.getType() == AttachmentType.IPS_PATCH).findFirst();
      
      if (attachment.isPresent())
      {
        patch = new IPSPatch(attachment.get().getPath());
        System.out.println("Automatic patch on "+handle.getGame().getTitle());
      }
      
    }
    
    if (patch != null)
      patch.apply(handle.getBuffer());
    
    return handle;
  }
}
