package jack.rm.data.set;

import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

class _3DS extends RomSetOfflineList
{
	_3DS() throws MalformedURLException
	{
		super(System._3DS, ProviderID.ADVANSCENE, new Dimension(268,240), new Dimension(268,240), new URL("http://www.advanscene.com/offline/imgs/ADVANsCEne_3DS/"));
	}
}