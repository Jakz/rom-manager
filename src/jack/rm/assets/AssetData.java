package jack.rm.assets;

import java.nio.file.Path;

public class AssetData
{
  private Path path;
  private String urlData;
  private long crc;
  
  public AssetData() { }

  public void setCRC(long crc) { this.crc = crc; }
  public void setPath(Path path) { this.path = path; }
  public void setURLData(String urlData) { this.urlData = urlData; }
  
  public long getCRC() { return crc; }
  public Path getPath() { return path; }
  public String getURLData() { return urlData; }
}
