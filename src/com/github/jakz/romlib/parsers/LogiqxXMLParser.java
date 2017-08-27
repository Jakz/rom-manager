package com.github.jakz.romlib.parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.game.RomSize;
import com.github.jakz.romlib.data.set.DataSupplier;
import com.github.jakz.romlib.data.set.GameList;
import com.pixbits.lib.io.xml.XMLHandler;
import com.pixbits.lib.io.xml.XMLParser;

public class LogiqxXMLParser extends XMLHandler<LogiqxXMLParser.Data>
{
  public static class Data
  {
    public Map<String, String> setAttributes;
    public GameList list;
  }
 
  private enum Status
  {
    NOWHERE,
    HEADER,
  }
  
  Supplier<Game> gameFactory = () -> new Game();
  
  Status status;
  
  RomSize.Set sizeSet;
  
  Map<String, String> attributes;
  
  List<Game> games;
  
  Game game;
  String gameName;
  String gameDescription;
  
  Rom rom;
  String romName;
  long size;
  long crc;
  byte[] md5;
  byte[] sha1;
  
  public void setGameFactory(Supplier<Game> gameFactory)
  {
    this.gameFactory = gameFactory;
  }
  
  @Override
  protected void init()
  {
    status = Status.NOWHERE;
    games = new ArrayList<>();
    sizeSet = new RomSize.Set();
    attributes = new HashMap<>();
  }

  @Override
  protected void start(String ns, String name, Attributes attr) throws SAXException
  {
    if (name.equals("header"))
      status = Status.HEADER;
    else if (status != Status.HEADER)
    {
      switch (name)
      {
        case "game": gameName = attrString("name"); break;
        case "rom":
        {
          romName = attrString("name");
          size = longAttributeOrDefault("size", -1);
          crc = longHexAttributeOrDefault("crc", -1L);
          md5 = hexByteArray("md5");
          sha1 = hexByteArray("sha1");
          break;
        }
      }
    }
  }

  @Override
  protected void end(String ns, String name) throws SAXException
  {
    if (status == Status.HEADER)
    {
      switch (name)
      {
        case "header": status = Status.NOWHERE; break;
        default: attributes.put(name, asString()); break;
      }
    }
    else
    {
      switch (name)
      {
        case "description": gameDescription = asString(); break;
        case "game":
        {
          game = gameFactory.get();
          
          game.setRom(rom);
          game.setTitle(gameName);
          game.setDescription(gameDescription);
          games.add(game); 
          
          game = null; 
          rom = null;
          break;
        }
        case "rom": rom = new Rom(romName, sizeSet.forBytes(size), crc, md5, sha1); break;
        case "datafile": 
        {
          //Provider provider = new Provider(name, description, version, "", author);
          data = new Data();
          data.setAttributes = attributes;
          data.list = new GameList(games.toArray(new Game[games.size()]), sizeSet);
          break;
        }
      }
    }
  }

  private Data data;
  @Override public Data get() { return data; }
  
  public static Data load(Path path) throws IOException, SAXException
  {
    LogiqxXMLParser xparser = new LogiqxXMLParser();
    XMLParser<Data> parser = new XMLParser<>(xparser);
    parser.load(path);
    return xparser.get();
  }
}
