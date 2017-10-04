package com.github.jakz.romlib.ui.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Text implements I18N
{ 
  ATTRIBUTE_GBC_GB_COMPATIBLE,
  ATTRIBUTE_GBC_SGB_ENHANCED,
  ATTRIBUTE_GBC_RUMBLE_VERSION,
  
  LOCATION_EUROPE,
  LOCATION_USA,
  LOCATION_GERMANY,
  LOCATION_CHINA,
  LOCATION_SPAIN,
  LOCATION_FRANCE,
  LOCATION_ITALY,
  LOCATION_JAPAN,
  LOCATION_NETHERLANDS,
  LOCATION_DENMARK,
  LOCATION_NORWAY,
  LOCATION_FINLAND,
  LOCATION_PORTUGAL,
  LOCATION_POLAND,
  LOCATION_GREECE,
  LOCATION_AUSTRALIA,
  LOCATION_CANADA,
  LOCATION_KOREA,
  LOCATION_SWEDEN,
  LOCATION_BRAZIL,
  LOCATION_RUSSIA,
  LOCATION_CROATIA,
  LOCATION_TAIWAN,
  LOCATION_HONG_KONG,
  
  LOCATION_ASIA,
  LOCATION_USA_EUROPE,
  LOCATION_JAPAN_EUROPE,
  LOCATION_USA_JAPAN,
  
  LOCATION_WORLD,
  LOCATION_NONE,
  
  LANGUAGE_TITLE,
  LOCATION_TITLE,
  SIZE_TITLE,
  TEXT_SEARCH_IN_TITLE,
  
  
  ROM_INFO_TITLE,
  ROM_INFO_DESCRIPTION,
  ROM_INFO_NUMBER,
  ROM_INFO_IMAGE_NUMBER,
  ROM_INFO_PUBLISHER,
  ROM_INFO_GROUP,
  ROM_INFO_DUMP_DATE,
  ROM_INFO_SIZE,
  ROM_INFO_GENRE,
  ROM_INFO_VIDEO_FORMAT,
  ROM_INFO_TAG,
  GAME_INFO_EXPORT_TITLE,
  GAME_INFO_SIZE,
  ROM_INFO_LOCATION,
  ROM_INFO_INTERNAL_NAME,
  ROM_INFO_SERIAL,
  ROM_INFO_CRC,
  ROM_INFO_SHA1,
  ROM_INFO_MD5,
  ROM_INFO_FILE_NAME,
  ROM_INFO_LANGUAGES,
  ROM_INFO_CLONES,
  ROM_INFO_SAVE_TYPE,
  ROM_INFO_COMMENT,
  ROM_INFO_VERSION,
  ROM_INFO_LICENSED,
  ROM_INFO_PATH,
  ;
  
  private static final ResourceBundle res = ResourceBundle.getBundle("com.github.jakz.romlib.ui.i18n.Strings", Locale.ENGLISH);
  
  public String text()
  {
    return res.getString(this.name());
  }
  
  public String toString() { return text(); }
}
