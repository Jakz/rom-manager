package jack.rm.json.workflow;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import jack.rm.data.rom.Rom;
import jack.rm.files.IPSPatch;

public class IPSPatchOperation extends RomOperation
{
  Map<Rom, IPSPatch> patches;
  
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
  
  public String getName() { return "IPS Patcher"; }
  public String getDescription() { return "This operation applies an IPS patch to a ROM"; }
  
  public RomHandle apply(RomHandle handle)
  {
    IPSPatch patch = patches.get(handle.getRom());
    
    if (patch != null)
      patch.apply(handle.getBuffer());
    
    return handle;
  }
}
