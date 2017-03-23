package jack.rm.json;

import java.util.List;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.attachments.Attachment;
import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.GameID;
import com.github.jakz.romlib.data.game.GameStatus;

public class GameSavedState
{
	GameID<?> id;
	GameStatus status;
	boolean favourite;
	List<GameSavedAttribute> attributes;
	List<RomSavedState> roms;
	List<Attachment> attachments;
	
	public GameSavedState() { }
	
	public GameSavedState(Game game)
	{
	  this.id = game.getID();
	  this.status = game.getStatus();
	  this.favourite = game.isFavourite();
	 
	  this.attributes = game.getCustomAttributes()
	      .map( e -> new GameSavedAttribute(e.getKey(), e.getValue()))
	      .collect(Collectors.toList());
	  
	  roms = game.stream()
	      .map(r -> new RomSavedState(r.handle()))
	      .collect(Collectors.toList());
	  
	  if (game.getAttachments().size() != 0)
	    this.attachments = game.getAttachments().data();
	}
}
