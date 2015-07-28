package jack.rm.json;

import java.util.List;
import java.util.stream.Collectors;

import jack.rm.data.*;

public class RomSavedState
{
	RomID<?> id;
	RomPath file;
	RomStatus status;
	boolean favourite;
	List<RomSavedAttribute> attributes;
	
	public RomSavedState() { }
	
	public RomSavedState(Rom rom)
	{
	  this.id = rom.getID();
	  this.status = rom.status;
	  this.file = rom.getPath();
	  this.favourite = rom.isFavourite();
	  
	  this.attributes = rom.getCustomAttributes()
	      .map( e -> new RomSavedAttribute(e.getKey(), e.getValue()))
	      .collect(Collectors.toList());
	}
}
