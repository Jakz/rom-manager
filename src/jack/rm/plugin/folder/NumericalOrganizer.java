package jack.rm.plugin.folder;

import java.nio.file.Paths;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.nio.file.Path;

import jack.rm.data.Rom;
import jack.rm.data.NumberedRom;
import jack.rm.files.Organizer;

public class NumericalOrganizer extends FolderPlugin
{
  private int folderSize = 100;
  
  public NumericalOrganizer()
  {
    folderSize = 100;
  }
  
  @Override 
  public Path getFolderForRom(Rom rom)
  {
    int which = (((NumberedRom)rom).number - 1) / folderSize;
    String first = Organizer.formatNumber(folderSize*which+1);
    String last = Organizer.formatNumber(folderSize*(which+1));
    return Paths.get(first+"-"+last+java.io.File.separator);
  }
  
  @Override
  public JsonElement serialize()
  {
    JsonObject object = super.serialize().getAsJsonObject();
    object.add("folderSize", new JsonPrimitive(folderSize));
    return object;
  }
  
  @Override
  public void unserialize(JsonElement element)
  {
    folderSize = element.getAsJsonObject().get("folderSize").getAsInt();
  }
}
