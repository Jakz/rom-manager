package jack.rm.plugins.providers;

import jack.rm.data.romset.Provider;

public class KnownProviders
{
  public static Provider ADVAN_SCENE = new Provider() {
    @Override public String getTag() { return "as"; }
    @Override public String getName() { return "AdvanScene"; }
  };
  
  public static Provider NO_INTRO = new Provider() {
    @Override public String getTag() { return "ni"; }
    @Override public String getName() { return "NoIntro"; }
  };
  
  public static Provider OFFLINE_LIST = new Provider() {
    @Override public String getTag() { return "ol"; }
    @Override public String getName() { return "OfflineList"; }
  };
}
