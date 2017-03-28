package com.github.jakz.romlib.data.assets;

import java.awt.Dimension;
import java.nio.file.Path;

public class Asset
{
  final private AssetType type;
  final private Path basePath;
  final private boolean hasCRC;
  
  Asset(AssetType type, Path basePath)
  {
    this.type = type;
    this.hasCRC = true;
    this.basePath = basePath;
  }
  
  boolean hasCRC() { return hasCRC; }
  public AssetType getType() { return type; }
  public Path getPath() { return basePath; }
  
  public static class Image extends Asset
  {
    private final Dimension size;
    
    public Image(Path basePath, Dimension size)
    {
      super(AssetType.IMAGE, basePath);
      this.size = size;
    }
    
    public Dimension getSize() { return size; }
  }
}
