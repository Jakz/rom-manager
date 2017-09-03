package jack.rm.plugins.scanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.github.jakz.romlib.support.header.Rule;
import com.github.jakz.romlib.support.header.Signature;
import com.github.jakz.romlib.support.header.SkipHeaderHandle;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.io.archive.VerifierEntry;

import jack.rm.plugins.types.FormatSupportPlugin;

public abstract class HeaderSupportPlugin extends FormatSupportPlugin
{
  private final Map<Signature, Rule> rules;
  
  protected HeaderSupportPlugin()
  {
    rules = new HashMap<>();
  }
  
  protected void addRule(Signature signature, Rule rule)
  {
    rules.put(signature, rule);
  }
  
  
  @Override
  public VerifierEntry getSpecializedEntry(VerifierEntry entry)
  {
    Optional<Map.Entry<Signature, Rule>> rule = rules.entrySet().stream()
      .filter(StreamException.rethrowPredicate(e -> e.getKey().verify(entry)))
      .findAny();
    
    if (rule.isPresent())
      this.debug("Found potential header match for plugin "+this.getClass().getName()+" with "+entry.toString());
    
    return rule.isPresent() ? new SkipHeaderHandle(entry.getVerifierHandle(), rule.get().getValue()) : entry;
  }

}
