package jack.rm.plugins.providers.instrinsic;

import java.util.List;

import com.github.jakz.romlib.data.set.Provider;

import jack.rm.data.console.System;

public class DatHeader
{
  public static class DatAttribute
  {
    String name;
    String className;
  }
  
  String name;
  Provider provider;
  System system;
  List<DatAttribute> attributes;
}
