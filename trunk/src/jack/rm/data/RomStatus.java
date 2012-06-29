package jack.rm.data;

import java.awt.Color;

public enum RomStatus
{
	NOT_FOUND("Not found",new Color(195,0,0)),
	INCORRECT_NAME("Incorrect Name",new Color(255,179,0)),
	FOUND("Found",new Color(0,150,0));
	
	public final String name;
	public final Color color;
	
	RomStatus(String name, Color color)
	{
		this.name = name;
		this.color = color;
	}
}
