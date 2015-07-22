package jack.rm.data.parser;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import jack.rm.data.set.RomSet;

public class XMLDatLoader implements DatLoader
{
  XMLHandler handler;
  //String path;
  
  public XMLDatLoader(XMLHandler handler)
  {
    this.handler = handler;
    //this.path = path;
  }
  
  public void load(RomSet<?> set)
  {
    try
    {
      handler.setRomSet(set);
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(handler);
      reader.parse(set.datPath());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
