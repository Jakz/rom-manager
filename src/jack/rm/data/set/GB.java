package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;

class GB extends RomSetOfflineList
{
	public GB() throws MalformedURLException
	{
		super(System.GB, ProviderID.NOINTRO, new Dimension(320,288), new Dimension(320,288), new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20Gameboy/"));
	}
}