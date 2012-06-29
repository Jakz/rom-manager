package jack.rm.data.parser;

import jack.rm.Main;
import jack.rm.data.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.io.CharArrayWriter;

class AdvanceSceneXMLParser extends DefaultHandler
{
	CharArrayWriter buffer = new CharArrayWriter();
	
	Rom rom;
	int curString;
	
	boolean started = false;
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes attr) throws SAXException
	{			
		if (localName.equals("game"))
		{
			rom = new Rom();
		}
		else if (localName.equals("games"))
		{
			started = true;
		}
		
		buffer.reset();
	}
	
	public String asString()
	{
		return buffer.toString().replaceAll("[\r\n]"," ").trim();
	}
	
	public int asInt()
	{
		return Integer.parseInt(asString());
	}
	
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		if (!started)
			return;
		
		if (localName.equals("imageNumber")) {
			rom.imageNumber = asInt();
		}
		else if (localName.equals("releaseNumber")) {
			rom.number = asInt();
		}
		else if (localName.equals("title")) {
			rom.title = asString();
		}
		else if (localName.equals("saveType")) {
			rom.save = asString();
			
			for (RomSave s : RomSave.values())
				if (rom.save.toLowerCase().contains(s.name.toLowerCase()))
					rom.saveType = s;
		}
		else if (localName.equals("romSize")) {
			rom.size = RomSize.forBytes(asInt());
		}
		else if (localName.equals("publisher"))
		{
			rom.publisher = asString();
		}
		else if (localName.equals("location"))
		{
			rom.location = Location.get(asInt());
		}
		else if (localName.equals("language"))
		{
			rom.languages = asInt();
		}
		else if (localName.equals("sourceRom"))
		{
			rom.group = asString();
		}
		// language
		else if (localName.equals("romCRC"))
		{
			rom.crc = Long.parseLong(asString(), 16);
		}
		else if (localName.equals("im1CRC"))
		{
			rom.imgCRC1 = Long.parseLong(asString(), 16);
		}
		else if (localName.equals("im2CRC"))
		{
			rom.imgCRC1 = Long.parseLong(asString(), 16);
		}
		else if (localName.equals("comment"))
		{
			rom.info = asString();
		}
		else if (localName.equals("game"))
		{
			//if (rom.number > 0)
				Main.romList.add(rom);
			
			Main.romList.sort();
		}
	
	}
	
	public void characters(char[] ch, int start, int length)
	{
		buffer.write(ch,start,length);
	}
}