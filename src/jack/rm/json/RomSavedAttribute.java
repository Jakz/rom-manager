package jack.rm.json;

import jack.rm.data.rom.Attribute;

public class RomSavedAttribute
{
  Attribute key;
  Object value;
  
  RomSavedAttribute() { }
  
  RomSavedAttribute(Attribute key, Object value)
  {
    this.key = key;
    this.value = value;
  }
}
