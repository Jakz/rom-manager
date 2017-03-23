package jack.rm.files.parser;

import java.util.function.Supplier;

import org.xml.sax.helpers.DefaultHandler;

import com.github.jakz.romlib.data.set.DatLoader;
import com.github.jakz.romlib.data.set.GameSet;

public abstract class XMLHandler extends DefaultHandler implements Supplier<DatLoader.Data>
{
  protected GameSet set;
  public void setRomSet(GameSet set) { this.set = set; }
}
