package jack.rm.plugins;

import java.nio.file.Path;
import java.util.Set;

public interface PluginWithIgnorePaths
{
  public Set<Path> getIgnoredPaths();
}
