package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

class GBC extends RomSetOfflineList
{
	public GBC() throws MalformedURLException
	{
		super(System.GBC, ProviderID.NOINTRO, new Dimension(320,288), new Dimension(320,288), new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy%20Color/"));
	}

}