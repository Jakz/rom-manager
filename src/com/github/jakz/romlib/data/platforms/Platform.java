package com.github.jakz.romlib.data.platforms;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;

import com.github.jakz.romlib.ui.Icon;

public interface Platform
{
  public String getTag();
  public String getName();
  public ImageIcon getIcon();
  public String[] fileExtensions();

  default public String defaultFileExtension() { return fileExtensions()[0]; }  
}
