package jack.rm.plugins.scanners;

import com.github.jakz.romlib.support.header.Rule;
import com.github.jakz.romlib.support.header.Signature;

public class NesHeaderSupportPlugin extends HeaderSupportPlugin
{
  public NesHeaderSupportPlugin()
  {
    super();
    addRule(Signature.of("NES\r"), Rule.of(16));
  }
}
