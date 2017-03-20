package jack.rm.plugins.scanners;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Game;
import com.pixbits.lib.io.archive.Verifier;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.VerifierOptions;
import com.pixbits.lib.io.archive.VerifierResult;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.NestedArchiveBatch;
import com.pixbits.lib.io.digest.DigestOptions;
import com.pixbits.lib.io.digest.Digester;
import com.pixbits.lib.io.digest.HashCache;
import com.pixbits.lib.lang.Pair;

import jack.rm.data.romset.GameSet;
import jack.rm.files.ScanResult;

public class DigestVerifier extends VerifierPlugin
{
  Verifier<Game> verifier;
  
  @Override
  public void setup(GameSet romset)
  {
    HashCache<Game> cache = romset.list.getCache();
    
    VerifierOptions options = new VerifierOptions();
    options.checkNestedArchives = true;
    options.matchSize = true;
    options.matchSHA1 = false;
    options.matchMD5 = false;
    
    DigestOptions doptions = new DigestOptions(options, true);
    Digester digester = new Digester(doptions);
    
    verifier = new Verifier<>(options, digester, cache);
  }
  
  @Override
  public List<ScanResult> verifyHandle(VerifierEntry handle) throws VerifierException
  {
    try
    {
      List<VerifierResult<Game>> result = verifier.verify(handle);
      
      return result.stream()
        .map(vr -> new ScanResult(vr.element, vr.handle))
        .collect(Collectors.toList());
    } 
    catch (NoSuchAlgorithmException | IOException e)
    {
      throw new VerifierException(e);
    }
  }
}
