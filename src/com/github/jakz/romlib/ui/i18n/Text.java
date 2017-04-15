package com.github.jakz.romlib.ui.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Text implements I18N
{ 
  ATTRIBUTE_GBC_GB_COMPATIBLE,
  ATTRIBUTE_GBC_SGB_ENHANCED
  ;
  
  private static final ResourceBundle res = ResourceBundle.getBundle("com.github.jakz.romlib.ui.i18n.Strings", Locale.ENGLISH);
  
  public String text()
  {
    return res.getString(this.name());
  }
  
  public String toString() { return text(); }
}
