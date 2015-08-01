package jack.rm.plugins.providers.instrinsic;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


public class ASParserMain
{
  static class RomEntry
  {
    int number;
    String title;
    String region;
    String languages;
    String genre;
    String publisher;
    String group;
    String date;
    String filename;
    String internalName;
    String serial;
    String version;
    String crc;
    String complement;
    String size;
    String saveType;
    String releaseNotes;
    String trainerPatch;
    String crackSavePatch;
    String purePatch;
    String saveGame;
    String relatedReleases;
    String pocketHeavenRelease;
    
    String assetTitle;
    String assetGame;
  }

  static List<RomEntry> entries = Collections.synchronizedList(new ArrayList<RomEntry>());
  
  static public void parseField(RomEntry entry, String name, String value)
  {
    switch (name)
    {
      case "Region": entry.region = value; break;
      case "Language(s)": entry.languages = value; break;
      case "Genre": entry.genre = value; break;
      case "Publishing Company": entry.publisher = value; break;
      case "Group": entry.group = value; break;
      case "Date": entry.date = value; break;
      case "Filename": entry.filename = value; break;
      case "Internal Name": entry.internalName = value; break;
      case "Serial": entry.serial = value; break;
      case "Version": entry.version = value; break;
      case "Crc32": entry.crc = value; break;
      case "Complement": entry.complement = value; break;
      case "Size": entry.size = value; break;
      case "Save Type": entry.saveType = value; break;
      case "Release Notes": entry.releaseNotes = value; break;
      case "Trainer Patch": entry.trainerPatch = value; break;
      case "Crack & Save Patch": entry.crackSavePatch = value; break;
      case "Pure Patch": entry.purePatch = value; break;
      case "SaveGame": entry.saveGame = value; break;
      case "Related Releases": entry.relatedReleases = value; break;
      case "PH's Release Number": entry.pocketHeavenRelease = value; break;
      default: throw new RuntimeException("Error on rom "+entry.number+": field \'"+name+"\' not mapped to anything.");
    }
  }
  
  static public void parse(int number)
  {
    try
    {
      Document doc = Jsoup.connect("http://www.advanscene.com/html/Releases/dbrelgb.php?id="+number).get();

      Elements table = doc.select("html > body > table > tbody > tr");
      Elements el = table.get(0).children();
      
      Elements images = el.get(1).select("img");
      Elements fields = el.get(0).select("tbody > tr");
      
      String title = doc.select("html > body").get(0).child(3).select("font").text();
            
      if (fields.size() != 26)
        throw new RuntimeException("Error on rom "+number+": not 26 fields as required.");
      
      final int FIELD_COUNT = 26;
      
      RomEntry entry = new RomEntry();
      entry.title = title;
      entry.number = number;
      
      for (int i = 0; i < FIELD_COUNT; ++i)
      {
        if (i == 0 || i == 5 || i == 9 || i == 17 || i == 25)
          continue;
        
        String fieldName = fields.get(i).child(0).text().trim();
        String fieldValue = fields.get(i).child(1).text().trim();
        
        parseField(entry, fieldName, fieldValue);
      }

      String assetTitle = images.get(0).attr("src");
      String assetGame = images.get(1).attr("src");
      
      entry.assetTitle = assetTitle;
      entry.assetGame = assetGame;
      
      entries.add(entry);

    } catch (IOException e)
    {
      e.printStackTrace();
    }
  }
  
  static public ThreadPoolExecutor pool;

  static public class ParseTask implements Callable<Boolean>
  {
    private final int number;
    
    ParseTask(int number)
    {
      this.number = number;
    }
    
    public Boolean call()
    {
      try
      {
        System.out.println("Parsing "+number);
        parse(number);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
      return true;
    }
  }
  
  public static void parse()
  {
    GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
    Gson gson = builder.create();

    pool = (ThreadPoolExecutor)Executors.newFixedThreadPool(10);

    for (int i = 0; i < 1475; ++i)
      pool.submit(new ParseTask(i));
    
    pool.shutdown();

    try
    {
      pool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

      
      BufferedWriter wrt = Files.newBufferedWriter(Paths.get("gb.json"), StandardOpenOption.CREATE);
      wrt.write(gson.toJson(entries));
      wrt.close();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args)
  {
    checkDat();
  }
  
  public static void checkDat()
  {
    try
    {  
      BufferedReader rdr = Files.newBufferedReader(Paths.get("dat/gb.json"));
      GsonBuilder builder = new GsonBuilder();
      Gson gson = builder.create();
      entries = gson.fromJson(rdr, new TypeToken<List<RomEntry>>(){}.getType());
      checkFields();
      
      for (int i = 0; i < fields.length; ++i)
        if (isUsingField[i])
          System.out.println("Field \'"+fields[i].getName()+"\', emptySomewhere: "+isNotUsingField[i]);
      
      System.out.println("");
      
      for (RomEntry entry : emptyEntries)
      {
        System.out.println("Empty entry: "+entry.number+" "+entry.title);
      }
      
      Set<String> saves = new HashSet<>();
      Set<String> versions = new HashSet<>();
      Set<String> regions = new HashSet<>();
      Set<String> languages = new HashSet<>();
      for (RomEntry entry : entries)
      {
        if (!emptyEntries.contains(entry))
        {
          saves.add(entry.saveType);
          versions.add(entry.version);
          regions.add(entry.region);
          languages.add(entry.languages);
        }
      }
      
      for (String save : saves)
        System.out.println("Save type: "+save);
      
      for (String version : versions)
        System.out.println("Version: "+version);
      
      for (String region : regions)
        System.out.println("Region: "+region);
      
      for (String language : languages)
        System.out.println("Language: "+language);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  static Field[] fields;
  static boolean[] isUsingField;
  static boolean[] isNotUsingField;
  static Set<RomEntry> emptyEntries;
  
  public static void checkFields()
  {
    fields = RomEntry.class.getDeclaredFields();
    isUsingField = new boolean[fields.length];
    isNotUsingField = new boolean[fields.length];
    emptyEntries = new HashSet<>();

    for (RomEntry entry : entries)
      checkFields(entry);
  }
  
  public static void checkFields(RomEntry entry)
  {    
    try
    {
      for (int i = 0; i < fields.length; ++i)
      {
        Field field = fields[i];
        Object value = field.get(entry);

        if (value.getClass().equals(Integer.class))
        {
          isUsingField[i] = true;
        }
        else if (value.getClass().equals(String.class))
        {
          if (value.equals("n/a"))
            isNotUsingField[i] = true;
          
          if (value.equals(""))
            emptyEntries.add(entry);
          else if (!value.equals("n/a"))
          {
            isUsingField[i] = true;
          }
          
        }
        else throw new RuntimeException("Unexpected class: "+value.getClass().getName());
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  public static enum Language
  {
    
  }
  
  public static enum Location
  {
    
  }
  
  class JsonRomField
  {
    Location location;
    Set<Language> languages;
  }
  
  /*
    GB
    
    Field 'number', used: true
    Field 'title', used: true
    Field 'region', used: true **
    Field 'languages', used: true **
    Field 'publisher', used: true
    Field 'group', used: true
    Field 'version', used: true
    Field 'crc', used: true
    Field 'size', used: true
    Field 'saveType', used: true
    Field 'releaseNotes', used: true
    Field 'pocketHeavenRelease', used: true
    Field 'assetTitle', used: true
    Field 'assetGame', used: true
   
   
   */
}
