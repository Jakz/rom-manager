package com.github.jakz.romlib.support.cso;

import java.nio.file.Path;

public class CSOWriter
{
  private final Path path;
  private CSOInfo info;
  
  public CSOWriter(Path path)
  {
    this.path = path;
  }
}
