package jack.rm.plugin;

public class PluginVersion implements Comparable<PluginVersion>
{
  final int major;
  final int minor;
  
  PluginVersion(int major, int minor)
  {
    this.major = major;
    this.minor = minor;
  }
  
  public String toString() { return major+"."+minor; }
  
  public int compareTo(PluginVersion version)
  {
    int deltaMajor = version.major - major;
    int deltaMinor = version.minor - minor;
    
    return deltaMajor != 0 ? deltaMajor : deltaMinor;
  }
  
  public boolean equals(Object object)
  {
    return object instanceof PluginVersion && ((PluginVersion)object).major == major && ((PluginVersion)object).minor == minor;
  }
}
