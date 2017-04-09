package jack.rm.files.parser;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.GameSet;

public abstract class XMLDatLoader implements DataSupplier
{
  XMLHandler handler;
  //String path;
  
  public XMLDatLoader(XMLHandler handler)
  {
    this.handler = handler;
    //this.path = path;
  }
  
  @Override
  public DataSupplier.Data load(GameSet set)
  {
    try
    {
      handler.setRomSet(set);
      XMLReader reader = XMLReaderFactory.createXMLReader();
      reader.setContentHandler(handler);
      reader.parse(set.datPath().toString());
      return handler.get();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return null;
    }
  }
}
