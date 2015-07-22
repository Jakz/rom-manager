package jack.rm.data.parser;

import org.xml.sax.helpers.DefaultHandler;

import jack.rm.data.set.RomSet;

public class XMLHandler extends DefaultHandler
{
  protected RomSet<?> set;
  
  public void setRomSet(RomSet<?> set) { this.set = set; }
}
