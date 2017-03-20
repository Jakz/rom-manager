package jack.rm.files.parser;

import org.xml.sax.helpers.DefaultHandler;

import jack.rm.data.romset.GameSet;

public class XMLHandler extends DefaultHandler
{
  protected GameSet set;
  
  public void setRomSet(GameSet set) { this.set = set; }
}
