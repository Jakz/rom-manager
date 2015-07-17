package jack.rm.net;

import java.net.URL;

import jack.rm.data.Asset;
import jack.rm.data.Rom;

public interface AssetDownloader
{
  public URL assetURL(Asset asset, Rom rom);
}
