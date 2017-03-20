package jack.rm.files.parser;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import jack.rm.data.romset.GameSet;

public abstract class XMLDatLoader implements DatLoader
{
  XMLHandler handler;
  //String path;
  
  public XMLDatLoader(XMLHandler handler)
  {
    this.handler = handler;
    //this.path = path;
  }
  
  public void load(GameSet set)
  {
    try
    {
      handler.setRomSet(set);
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(handler);
      reader.parse(set.datPath().toString());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
