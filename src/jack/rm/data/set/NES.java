package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

class NES extends RomSetOfflineList
{
	public NES() throws MalformedURLException
	{
		super(System.NES, ProviderID.NOINTRO, new Dimension(256,240), new Dimension(256,240), new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Nintendo%20NES%20-%20Famicom/"));
	}
	
	public boolean supportsNumberedRoms() { return false; }
}