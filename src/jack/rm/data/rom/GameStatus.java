package jack.rm.data.rom;

import java.awt.Color;

public enum GameStatus
{
	MISSING("Missing",new Color(195,0,0)),
	UNORGANIZED("Unorganized",new Color(255,179,0)),
	FOUND("Found",new Color(0,150,0));
	
	public final String name;
	public final Color color;
	
	GameStatus(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}
}
