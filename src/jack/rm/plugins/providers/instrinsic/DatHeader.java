package jack.rm.plugins.providers.instrinsic;

import java.util.List;

import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.data.set.Provider;

public class DatHeader
{
  public static class DatAttribute
  {
    String name;
    String className;
  }
  
  String name;
  Provider provider;
  Platform system;
  List<DatAttribute> attributes;
}
