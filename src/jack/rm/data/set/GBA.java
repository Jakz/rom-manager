package jack.rm.data.set;

import jack.rm.data.NumberedRom;
import jack.rm.data.Rom;
import jack.rm.data.console.System;

import java.awt.Dimension;
import java.net.URL;
import java.net.MalformedURLException;

public class GBA extends RomSetOfflineList implements NumberedSet<NumberedRom>
{

  
  public GBA() throws MalformedURLException
	{
		super(System.GBA, ProviderID.OFFLINELIST, new Dimension(480,320), new Dimension(480,320), new URL("http://offlinelistgba.free.fr/imgs/"));
	}

}