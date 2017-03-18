package jack.rm.plugins.scanners;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.pixbits.lib.io.archive.Verifier;
import com.pixbits.lib.io.archive.VerifierOptions;
import com.pixbits.lib.io.archive.handles.Handle;
import com.pixbits.lib.io.archive.handles.NestedArchiveBatch;
import com.pixbits.lib.io.digest.DigestOptions;
import com.pixbits.lib.io.digest.Digester;
import com.pixbits.lib.io.digest.HashCache;
import com.pixbits.lib.lang.Pair;

import jack.rm.data.rom.Rom;
import jack.rm.data.romset.RomSet;
import jack.rm.files.ScanResult;

public class DigestVerifier extends VerifierPlugin
{
  Verifier<Rom> verifier;
  
  @Override
  public void setup(RomSet romset)
  {
    HashCache<Rom> cache = romset.list.getCache();
    
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
  public ScanResult verifyHandle(Handle handle) throws VerifierException
  {
    try
    {
      return new ScanResult(verifier.verify(handle), handle);
    } 
    catch (NoSuchAlgorithmException | IOException e)
    {
      throw new VerifierException(e);
    }
  }

  @Override
  public List<ScanResult> verifyHandle(NestedArchiveBatch batch) throws VerifierException
  {
    List<ScanResult> pairs = new ArrayList<>();
    
    try
    {
      verifier.verifyNestedArchive(batch, (r, h) -> { pairs.add(new ScanResult(r,h)); });
    } 
    catch (NoSuchAlgorithmException | IOException e)
    {
      throw new VerifierException(e);
    }
    
    return pairs;
  }

}
