package jack.rm.json;

import jack.rm.data.*;

public class RomSavedState
{
	RomID<?> id;
	RomPath file;
	RomStatus status;
	boolean favourite;
	
	public RomSavedState() { }
	
	public RomSavedState(RomID<?> id, RomStatus status, RomPath file, boolean favourite)
	{
	  this.id = id;
	  this.status = status;
	  this.file = file;
	  this.favourite = favourite;
	}
}
