package jack.rm.plugins.providers.instrinsic;

import java.util.List;

import jack.rm.data.console.System;
import jack.rm.data.romset.Provider;

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
