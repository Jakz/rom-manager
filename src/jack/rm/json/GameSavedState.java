package jack.rm.json;

import java.util.List;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameStatus;
import com.github.jakz.romlib.data.game.Rom;

import jack.rm.data.attachment.Attachment;

public class GameSavedState
{
	GameStatus status;
	boolean favourite;
	List<RomSavedState> roms;
	List<RomSavedAttribute> attributes;
	List<Attachment> attachments;
	
	public GameSavedState() { }
	
	public GameSavedState(Game game)
	{
	  this.status = game.status;
	  this.favourite = game.isFavourite();
	  
	  this.roms = game.stream()
	    .filter(Rom::isPresent)
	    .map(r -> new RomSavedState(r.getID(), r.handle()))
	    .collect(Collectors.toList());
	 
	  this.attributes = game.getCustomAttributes()
	    .map( e -> new RomSavedAttribute(e.getKey(), e.getValue()))
	    .collect(Collectors.toList());
	  
	  if (game.getAttachments().size() != 0)
	    this.attachments = game.getAttachments().data();
	}
}
