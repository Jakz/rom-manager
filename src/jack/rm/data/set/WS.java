package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

class WS extends RomSetOfflineList
{
	public WS() throws MalformedURLException
	{
		super(System.WS, ProviderID.NOINTRO, new Dimension(448,448), new Dimension(448,448), new URL("http://nointro.free.fr/imgs/Official%20No-Intro%20Bandai%20WonderSwan/"));
	}
}