package jack.rm.data.set;

public enum Console
{
	NES("nes", "NES", new String[] {"nes", "rom", "unf"}),
	GB("gb", "GameBoy", new String[] {"gb"}),
	GBC("gbc", "GameBoy Color", new String[] {"gbc"}),
	GBA("gba", "GameBoy Advance",new String[] {"gba", "agb", "bin"}),
	NDS("nds", "Nintendo DS", new String[] {"nds", "dsi"}),
	WS("ws", "WonderSwan", new String[] {"ws"}),
	C64("c64", "Commodore 64", null)
	;
	
	public final String tag;
	public final String name;
	public final String[] exts;
	
	Console(String tag, String name, String[] exts)
	{
		this.tag = tag;
		this.name = name;
		this.exts = exts;
	}
}
