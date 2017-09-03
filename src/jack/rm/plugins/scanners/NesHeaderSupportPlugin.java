package jack.rm.plugins.scanners;

import com.github.jakz.romlib.support.header.Rule;
import com.github.jakz.romlib.support.header.Signature;

public class NesHeaderSupportPlugin extends HeaderSupportPlugin
{
  public NesHeaderSupportPlugin()
  {
    super();
    addRule(Signature.of(new byte[] {'N', 'E', 'S', 0x1A}), Rule.of(16));
  }
}
