package jack.rm.plugins.providers;

import com.github.jakz.romlib.data.set.Provider;

public class KnownProviders
{
  public static Provider ADVAN_SCENE = new Provider("AdvanScene", "as", null);
  public static Provider NO_INTRO = new Provider("NoIntro", "ni", null);
  public static Provider OFFLINE_LIST = new Provider("OfflineList", "ol", null);
  
  public static Provider REDUMP = new Provider("Redump", "redump", null);
  public static Provider GOOD_OLD_DAYS = new Provider("Good Old Days", "god", new Provider.Source("http://www.goodolddays.net/tgod_floppy_images.dat"));
  public static Provider MAME = new Provider("MAME", "mame", null);

}
