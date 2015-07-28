package jack.rm.json;

import jack.rm.data.rom.RomAttribute;

public class RomSavedAttribute
{
  RomAttribute key;
  Object value;
  
  RomSavedAttribute() { }
  
  RomSavedAttribute(RomAttribute key, Object value)
  {
    this.key = key;
    this.value = value;
  }
}
