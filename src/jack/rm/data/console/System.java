package jack.rm.data.console;

public enum System
{
	NES("nes", "NES", new String[] {"nes", "rom", "unf"}, true),
	GB("gb", "GameBoy", new String[] {"gb"}, true),
	GBC("gbc", "GameBoy Color", new String[] {"gbc"}, true),
	GBA("gba", "GameBoy Advance",new String[] {"gba", "agb", "bin"}, true),
	NDS("nds", "Nintendo DS", new String[] {"nds", "dsi"}, true),
	_3DS("3ds", "Nintendo 3DS", new String[] {"3ds"}, true),
	WS("ws", "WonderSwan", new String[] {"ws"}, true),
	GG("gg", "Game Gear", new String[] {"gg"}, true),
	C64("c64", "Commodore 64", null, false)
	;
	
	public final String tag;
	public final String name;
	public final boolean acceptsArchives;
	public final String[] exts;
	
	System(String tag, String name, String[] exts, boolean acceptsArchives)
	{
		this.tag = tag;
		this.name = name;
		this.exts = exts;
		this.acceptsArchives = acceptsArchives;
	}
}
