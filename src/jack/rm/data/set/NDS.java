package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

class NDS extends RomSetOfflineList
{
	NDS() throws MalformedURLException
	{
		super(System.NDS, ProviderID.ADVANSCENE, new Dimension(214,384), new Dimension(256,384), new URL("http://www.retrocovers.com/offline/imgs/ADVANsCEne_NDS/"));
	}
}