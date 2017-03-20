package jack.rm.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.pixbits.lib.io.archive.handles.Handle;

import jack.rm.data.attachment.Attachment;
import jack.rm.data.rom.Rom;
import jack.rm.data.rom.RomID;
import jack.rm.data.rom.GameStatus;

public class RomSavedState
{
	RomID<?> id;
	Handle file;
	GameStatus status;
	boolean favourite;
	List<RomSavedAttribute> attributes;
	List<Attachment> attachments;
	
	public RomSavedState() { }
	
	public RomSavedState(Rom rom)
	{
	  this.id = rom.getID();
	  this.status = rom.status;
	  this.file = rom.getHandle();
	  this.favourite = rom.isFavourite();
	 
	  this.attributes = rom.getCustomAttributes()
	      .map( e -> new RomSavedAttribute(e.getKey(), e.getValue()))
	      .collect(Collectors.toList());
	  
	  if (rom.getAttachments().size() != 0)
	    this.attachments = new ArrayList<Attachment>(rom.getAttachments());
	}
}
