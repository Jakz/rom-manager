package jack.rm.plugins;

import java.util.Set;
import java.nio.file.Path;

public interface PluginWithIgnorePaths
{
  public Set<Path> getIgnoredPaths();
}
