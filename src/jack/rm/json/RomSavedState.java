package jack.rm.json;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.google.gson.reflect.TypeToken;

import jack.rm.data.*;
import jack.rm.data.set.RomSet;
import jack.rm.log.*;

public class RomSavedState
{
	RomID<?> id;
	RomPath file;
	RomStatus status;
	
	public RomSavedState() { }
	
	public RomSavedState(RomID<?> id, RomStatus status, RomPath file)
	{
	  this.id = id;
	  this.status = status;
	  this.file = file;
	}
}
