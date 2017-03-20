package jack.rm.json;

import com.github.jakz.romlib.data.game.attributes.Attribute;

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
