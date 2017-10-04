package jack.rm.files;

import com.github.jakz.romlib.data.assets.Downloader;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.log.Log;
import com.pixbits.lib.log.Logger;

import jack.rm.Main;
import jack.rm.gui.Dialogs;
import jack.rm.log.LogTarget;

public class MyAssetDownloader extends Downloader
{
  protected static final Logger logger = Log.getLogger(Downloader.class);
  
  public MyAssetDownloader(GameSet set) 
  {
    super(set);
    onStart = () -> Main.progress.show(Main.mainFrame, "Asset Download", () -> { interrupt(); });
    onFinish = () -> Main.progress.finished();
    onProgress = (completed, total) -> Main.progress.update(completed/(float)total, (completed+1)+" of "+total);

    onWontStart = () -> Dialogs.showMessage("Asset Downloader", "All the assets have already been downloaded.", Main.mainFrame);
    onAssetDownloaded = g -> {
      // TODO: doesn't work if user changed the rom while it was downloading
      Main.mainFrame.updateInfoPanel(g);
    };
    onDownloadFailed = (game, url) -> {
      logger.e(LogTarget.game(game), "Asset not found at "+url);
      Main.mainFrame.updateInfoPanel(game);
    };
  }
}
