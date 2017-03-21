package jack.rm.json;

import com.github.jakz.romlib.data.game.RomID;
import com.pixbits.lib.io.archive.handles.Handle;

public class RomSavedState
{
  public RomID<?> id;
  public Handle handle;
  
  public RomSavedState(RomID<?> id, Handle handle)
  {
    this.id = id;
    this.handle = handle;
  }
  
  public RomSavedState() { }
}
