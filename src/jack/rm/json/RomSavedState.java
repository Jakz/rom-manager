package jack.rm.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.RomID;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.io.archive.handles.Handle;

import jack.rm.data.attachment.Attachment;

public class RomSavedState
{
	RomID<?> id;
	Handle file;
	GameStatus status;
	boolean favourite;
	List<RomSavedAttribute> attributes;
	List<Attachment> attachments;
	
	public RomSavedState() { }
	
	public RomSavedState(Game game)
	{
	  this.id = game.getID();
	  this.status = game.status;
	  this.file = game.getHandle();
	  this.favourite = game.isFavourite();
	 
	  this.attributes = game.getCustomAttributes()
	      .map( e -> new RomSavedAttribute(e.getKey(), e.getValue()))
	      .collect(Collectors.toList());
	  
	  if (game.getAttachments().size() != 0)
	    this.attachments = game.getAttachments().data();
	}
}
