package jack.rm.data.set;

public enum Console
{
	NES("nes", "NES"),
	GB("gb", "GameBoy"),
	GBC("gbc", "GameBoy Color"),
	GBA("gba", "GameBoy Advance"),
	NDS("nds", "Nintendo DS"),
	WS("ws", "WonderSwan")
	;
	
	public final String tag;
	public final String name;
	
	Console(String tag, String name)
	{
		this.tag = tag;
		this.name = name;
	}
}
