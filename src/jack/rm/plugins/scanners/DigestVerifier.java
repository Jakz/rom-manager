package jack.rm.plugins.scanners;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.jakz.romlib.data.game.Rom;
import com.github.jakz.romlib.data.set.GameSet;
import com.pixbits.lib.io.archive.Verifier;
import com.pixbits.lib.io.archive.VerifierEntry;
import com.pixbits.lib.io.archive.VerifierOptions;
import com.pixbits.lib.io.archive.VerifierResult;
import com.pixbits.lib.io.digest.DigestOptions;
import com.pixbits.lib.io.digest.Digester;
import com.pixbits.lib.io.digest.HashCache;
import jack.rm.files.ScanResult;
import jack.rm.plugins.types.VerifierPlugin;

public class DigestVerifier extends VerifierPlugin
{
  Verifier<Rom> verifier;
  
  @Override
  public void setup(GameSet romset)
  {
    HashCache<Rom> cache = romset.hashCache();
    
    VerifierOptions options = new VerifierOptions(true, false, false, true);
    
    DigestOptions doptions = new DigestOptions(options, true);
    Digester digester = new Digester(doptions);
    
    verifier = new Verifier<>(options, digester, cache);
  }
  
  @Override
  public void setEntryTransformer(Function<VerifierEntry, ? extends VerifierEntry> transformer)
  {
    verifier.setTransformer(transformer);
  }
  
  @Override
  public List<ScanResult> verifyHandle(VerifierEntry handle) throws VerifierException
  {
    try
    {
      List<VerifierResult<Rom>> result = verifier.verify(handle);
      
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
