package com.github.jakz.romlib.data.platforms;

import javax.swing.ImageIcon;


public interface Platform
{
  public String getTag();
  public String getName();
  public ImageIcon getIcon();
  public String[] fileExtensions();

  default public String defaultFileExtension() { return fileExtensions()[0]; }  
  
  public static Platform of(final String name)
  {
    return new Platform() {

      @Override public String getTag() { return ""; }
      @Override public String getName() { return name; }
      @Override public ImageIcon getIcon() { return null; }
      @Override public String[] fileExtensions() { return new String[] { "" }; }   
    };
  }
}
