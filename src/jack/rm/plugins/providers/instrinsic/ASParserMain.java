package jack.rm.plugins.providers.instrinsic;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import jack.rm.data.rom.Location;
import jack.rm.data.rom.RomAttribute;
import jack.rm.data.rom.Version;
import jack.rm.data.console.GB;
import jack.rm.data.console.GBC;
import jack.rm.data.rom.Attribute;
import jack.rm.data.rom.CustomRomAttribute;
import jack.rm.data.rom.Language;


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
  
  private static Map<String, Location> locationMap = new HashMap<>();
  private static Map<String, Language> languageMap = new HashMap<>();
  private static Map<String, SaveFactory> saveMap = new HashMap<>();
  private static Map<String, Integer> sizeMap = new HashMap<>();
  private static Map<String, VersionFactory> versionMap = new HashMap<>();


  private static class SaveFactory
  {
    final private GB.Save.Type type;
    final private long size;
    
    SaveFactory(GB.Save.Type type, long size) { this.type = type; this.size = size; }
    GB.Save build() { return new GB.Save(type, size); }
  }
  
  private static class VersionFactory
  {
    final private int major, minor;

    VersionFactory(int major, int minor) { this.major = major; this.minor = minor; }
    Version build() { return major != 0 || minor != 0 ? new Version.Standard(major, minor) : new Version.Unspecified(); }
  }
  
  static
  {
    locationMap.put("USA", Location.USA);
    locationMap.put("Sweden", Location.SWEDEN);
    locationMap.put("Japan", Location.JAPAN);
    locationMap.put("Europe", Location.EUROPE);
    locationMap.put("Italy", Location.ITALY);
    locationMap.put("France", Location.FRANCE);
    locationMap.put("Australia", Location.AUSTRALIA);
    locationMap.put("Germany", Location.GERMANY);
    locationMap.put("Spain", Location.SPAIN);
    locationMap.put("China", Location.CHINA);
    
    languageMap.put("English", Language.ENGLISH);
    languageMap.put("French", Language.FRENCH);
    languageMap.put("German", Language.GERMAN);
    languageMap.put("Italian", Language.ITALIAN);
    languageMap.put("Spanish", Language.SPANISH);
    languageMap.put("Danish", Language.DANISH);
    languageMap.put("Dutch", Language.DUTCH);
    languageMap.put("Japanese", Language.JAPANESE);
    languageMap.put("Swedish", Language.SWEDISH);
    languageMap.put("Portuguese (", Language.PORTUGUESE);
    languageMap.put("Portuguese", Language.PORTUGUESE);
    languageMap.put("Polish", Language.POLISH);
    languageMap.put("English (UK)", Language.ENGLISH_UK);
    languageMap.put("English (US)", Language.ENGLISH);
    languageMap.put("Chinese", Language.CHINESE);
    languageMap.put("Norwegian", Language.NORWEGIAN);
    languageMap.put("Finnish", Language.FINNISH);
    languageMap.put("UK English", Language.ENGLISH_UK);
    
    saveMap.put("256KBit", new SaveFactory(GB.Save.Type.SRAM, 32678));
    saveMap.put("64KBit", new SaveFactory(GB.Save.Type.SRAM, 8192));
    saveMap.put("No SRAM", new SaveFactory(GB.Save.Type.NONE, 0));
    //saveMap.put("n/a", new SaveFactory(GBC.Save.Type.NONE, 0));
    
    final int kbit256 = 32768;
    final int mbit1 = 65536*2;
    
    sizeMap.put("256 Kbit", kbit256);
    sizeMap.put("0,25 Mbit", kbit256);
    sizeMap.put("0,5 Mbit", kbit256*2);
    sizeMap.put("1 Mbit", mbit1);
    sizeMap.put("2 Mbit", mbit1*2);
    sizeMap.put("4 Mbit", mbit1*4);
    sizeMap.put("8 Mbit", mbit1*8);
    sizeMap.put("8,12 Mbit", mbit1*8);
    sizeMap.put("16 Mbit", mbit1*16);
    sizeMap.put("32 Mbit", mbit1*32);
    sizeMap.put("64 Mbit", mbit1*64);

    
    versionMap.put("1.0", new VersionFactory(1,0));
    versionMap.put("1.1)", new VersionFactory(1,1));
    versionMap.put("1.1", new VersionFactory(1,1));
    versionMap.put("1.16", new VersionFactory(1,16));
    versionMap.put("1.2", new VersionFactory(1,2));
    versionMap.put("n/a", new VersionFactory(0,0));

  }
  
  public static void checkDat()
  {
    try
    {  
      BufferedReader rdr = Files.newBufferedReader(Paths.get("dat/im-gb-as.json"));
      GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
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
      Set<String> sizes = new HashSet<>();
      for (RomEntry entry : entries)
      {
        if (!emptyEntries.contains(entry))
        {
          saves.add(entry.saveType);
          versions.add(entry.version);
          regions.add(entry.region);
          sizes.add(entry.size);
          Arrays.stream(entry.languages.split(" -")).map(String::trim).forEach(languages::add);
          
          String[] assets = { entry.assetGame, entry.assetTitle };
          
          for (String asset : assets)
          {
            int assetNumber = Integer.valueOf(asset.substring(asset.lastIndexOf("/")+1, asset.lastIndexOf(".")-1));
            if (assetNumber != entry.number)
              throw new RuntimeException("Asset and number doesn't match: "+entry.number+" != "+assetNumber);
          }
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
      
      for (String size : sizes)
        System.out.println("Size: "+size);
      
      List<JsonRomField> jentries = new ArrayList<>();
      for (RomEntry entry : entries)
      {
        if (!emptyEntries.contains(entry))
        {
          jentries.add(buildJsonEntry(entry));
        }
      }
      
      
      BufferedWriter wrt = Files.newBufferedWriter(Paths.get("gb.json"), StandardOpenOption.TRUNCATE_EXISTING);
      wrt.write(gson.toJson(jentries));
      wrt.close();
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
  
  static JsonRomField buildJsonEntry(RomEntry e)
  {
    JsonRomField j = new JsonRomField();
    
    if (!locationMap.containsKey(e.region))
      throw new RuntimeException("Location Missing: "+e.region);
    if (!sizeMap.containsKey(e.size))
      throw new RuntimeException("Size Missing: "+e.size+" ("+e.title+")");
    if (!saveMap.containsKey(e.saveType))
      throw new RuntimeException("Save Missing: "+e.saveType);
    if (!versionMap.containsKey(e.version))
      throw new RuntimeException("Version Missing: "+e.version);

    j.number = e.number;
    j.title = e.title;
    j.location = locationMap.get(e.region);
    j.languages = new ArrayList<>();
    
    StringBuilder missingLanguage = new StringBuilder();
    Arrays.stream(e.languages.split(" -")).map(String::trim).forEach(l -> {
      if (!languageMap.containsKey(l))
        missingLanguage.append(l+" ");
      else
        j.languages.add(languageMap.get(l));
    });
    
    if (missingLanguage.length() > 0)
      System.out.println("Language Missing: "+missingLanguage+" for \'"+e.languages+" ("+e.title+")");
    
    j.publisher = e.publisher;
    j.group = e.group;
    j.version = versionMap.get(e.version).build();
    j.crc = Long.parseLong(e.crc.toLowerCase(), 16);
    j.size = sizeMap.get(e.size);
    j.saveType = saveMap.get(e.saveType).build();
    j.comment = e.releaseNotes;
    j.pocketHeavenRef = Integer.valueOf(e.pocketHeavenRelease);
    
    j.populate();
    
    return j;
  }
  
  private final static CustomRomAttribute POCKET_HEAVEN_REF = new CustomRomAttribute("Pocket Heaven #", "pocket-heaven-reference", null);

  static class JsonRomField extends HashMap<Attribute, Object>
  {
    int number;
    String title;
    Location location;
    List<Language> languages;
    String publisher;
    String group;
    Version version;
    long crc;
    int size;
    GB.Save saveType;
    String comment;
    int pocketHeavenRef;
    
    void populate()
    {
      this.put(RomAttribute.NUMBER, number);
      this.put(RomAttribute.TITLE, title);
      this.put(RomAttribute.LOCATION, location);
      this.put(RomAttribute.LANGUAGE, languages);
      this.put(RomAttribute.PUBLISHER, publisher);
      this.put(RomAttribute.GROUP, group);
      if (!(version instanceof Version.Unspecified))
        this.put(RomAttribute.VERSION, version);
      this.put(RomAttribute.CRC, crc);
      this.put(RomAttribute.SIZE, size);
      this.put(RomAttribute.SAVE_TYPE, saveType);
      this.put(RomAttribute.COMMENT, comment);
      this.put(POCKET_HEAVEN_REF, pocketHeavenRef);
    }
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
    
    Field 'number', emptySomewhere: false
    Field 'title', emptySomewhere: false **
    Field 'region', emptySomewhere: false **
    Field 'languages', emptySomewhere: false **
    Field 'publisher', emptySomewhere: false **
    Field 'group', emptySomewhere: false **
    Field 'version', emptySomewhere: true
    Field 'crc', emptySomewhere: false
    Field 'size', emptySomewhere: false **
    Field 'saveType', emptySomewhere: false **
    Field 'releaseNotes', emptySomewhere: false
    Field 'pocketHeavenRelease', emptySomewhere: false
    Field 'assetTitle', emptySomewhere: false **
    Field 'assetGame', emptySomewhere: false **
   
   
   */
}
