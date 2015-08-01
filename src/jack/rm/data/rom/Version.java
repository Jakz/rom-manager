package jack.rm.data.rom;

public interface Version
{
  public static class Unspecified implements Version
  {
    
  }
  
  public static class Standard implements Version
  {
    int major;
    int minor;
    
    public Standard(int major, int minor)
    {
      this.major = major;
      this.minor = minor;
    }
    
    public int getMajor() { return major; }
    public int getMinor() { return minor; }
    public String toString() { return major != 0 || minor != 0 ? (major + "." + minor) : ""; }
  }
}
