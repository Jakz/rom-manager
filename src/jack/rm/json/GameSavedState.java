package jack.rm.json;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.attachments.Attachment;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.game.GameStatus;
import com.pixbits.lib.io.archive.handles.Handle;

public class GameSavedState
{
	GameID<?> id;
	Handle file;
	GameStatus status;
	boolean favourite;
	List<GameSavedAttribute> attributes;
	List<Attachment> attachments;
	
	public GameSavedState() { }
	
	public GameSavedState(Game rom)
	{
	  this.id = rom.getID();
	  this.status = rom.status;
	  this.file = rom.getHandle();
	  this.favourite = rom.isFavourite();
	 
	  this.attributes = rom.getCustomAttributes()
	      .map( e -> new GameSavedAttribute(e.getKey(), e.getValue()))
	      .collect(Collectors.toList());
	  
	  if (rom.getAttachments().size() != 0)
	    this.attachments = rom.getAttachments().data();
	}
}
