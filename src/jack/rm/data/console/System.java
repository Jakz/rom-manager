package jack.rm.data.console;

import jack.rm.gui.Icon;

public enum System
{
	NES("nes", "NES", new String[] {"nes", "rom", "unf"}, Icon.SYSTEM_NES, true),
	GB("gb", "GameBoy", new String[] {"gb"}, Icon.SYSTEM_GAMEBOY, true),
	GBC("gbc", "GameBoy Color", new String[] {"gbc"}, Icon.SYSTEM_GAMEBOY, true),
	GBA("gba", "GameBoy Advance",new String[] {"gba", "agb", "bin"}, Icon.SYSTEM_GAMEBOY_ADVANCE, true),
	NDS("nds", "Nintendo DS", new String[] {"nds", "dsi"}, true),
	_3DS("3ds", "Nintendo 3DS", new String[] {"3ds"}, true),
	WS("ws", "WonderSwan", new String[] {"ws"}, Icon.SYSTEM_WONDERSWAN, true),
	GG("gg", "Game Gear", new String[] {"gg"}, Icon.SYSTEM_GAME_GEAR, true),
	C64("c64", "Commodore 64", null, false)
	;
	
	public final String tag;
	public final String name;
	public final boolean acceptsArchives;
	public final String[] exts;
	public final Icon icon;
	
	System(String tag, String name, String[] exts, Icon icon, boolean acceptsArchives)
	{
		this.tag = tag;
		this.name = name;
		this.exts = exts;
		this.acceptsArchives = acceptsArchives;
		this.icon = icon;
	}
	
	System(String tag, String name, String[] exts, boolean acceptsArchives)
	{
	  this(tag, name, exts, null, acceptsArchives);
	}
}
