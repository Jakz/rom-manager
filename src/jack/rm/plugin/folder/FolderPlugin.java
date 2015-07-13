package jack.rm.plugin.folder;

import jack.rm.data.Rom;
import jack.rm.json.Jsonnable;
import jack.rm.plugin.Plugin;

import java.nio.file.Path;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class FolderPlugin implements Jsonnable, Plugin
{
  public abstract Path getFolderForRom(Rom rom);
  
  @Override
  public JsonElement serialize()
  {
    JsonObject json = new JsonObject();
    json.add("class", new JsonPrimitive(this.getClass().getName()));
    return json;
  }
  
  @Override
  public void unserialize(JsonElement element) { }
}
