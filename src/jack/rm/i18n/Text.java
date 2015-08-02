package jack.rm.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Text
{
	LOCATION_TITLE,
	LOCATION_EUROPE,
	LOCATION_USA,
	LOCATION_GERMANY,
	LOCATION_CHINA,
	LOCATION_SPAIN,
	LOCATION_FRANCE,
	LOCATION_ITALY,
	LOCATION_JAPAN,
	LOCATION_NETHERLANDS,
	LOCATION_AUSTRALIA,
	LOCATION_KOREA,
	LOCATION_SWEDEN,
	
	LANGUAGE_TITLE,

	ROM_INFO_TITLE,
	ROM_INFO_NUMBER,
	ROM_INFO_PUBLISHER,
	ROM_INFO_GROUP,
	ROM_INFO_DUMP_DATE,
	ROM_INFO_SIZE,
	ROM_INFO_GENRE,
	ROM_INFO_TAG,
	ROM_INFO_LOCATION,
	ROM_INFO_INTERNAL_NAME,
	ROM_INFO_SERIAL,
	ROM_INFO_CRC,
	ROM_INFO_SHA1,
	ROM_INFO_MD5,
	ROM_INFO_LANGUAGES,
	ROM_INFO_CLONES,
	ROM_INFO_SAVE_TYPE,
	ROM_INFO_COMMENT,
	ROM_INFO_VERSION,
	ROM_INFO_PATH,
	ROM_INFO_FILENAME,
	
	MENU_ROMS_TITLE,
	MENU_ROMS_SCAN_FOR_ROMS,
	MENU_ROMS_SCAN_FOR_NEW_ROMS,
	MENU_ROMS_CLEANUP,
	MENU_ROMS_EXPORT,
	MENU_ROMS_EXPORT_MISSING,
	MENU_ROMS_EXPORT_FOUND,
	MENU_ROMS_EXIT,
	MENU_ROMS_RENAME,
	
	MENU_VIEW_TITLE,
	MENU_VIEW_SHOW_CORRECT,
	MENU_VIEW_SHOW_NOT_FOUND,
	MENU_VIEW_SHOW_UNORGANIZED,
	
	MENU_TOOLS_TITLE,
	MENU_TOOLS_OPTIONS,
	MENU_TOOLS_ASSETS,
	MENU_TOOLS_DOWNLOAD_ASSETS,
	MENU_TOOLS_PACK_ASSETS,
	MENU_TOOLS_SHOW_MESSAGES,
	MENU_TOOLS_CONSOLE,
	
	MENU_LANGUAGE_TITLE,
	
	MENU_HELP_TITLE,
	
	TEXT_SEARCH_IN_TITLE,
	TEXT_RENAMER_PATTERN,
	TEXT_RENAMER_EXAMPLE,
	TEXT_CLOSE,
	
	OPTION_RENAMER,
	OPTION_ROMSET,
	OPTION_PLUGINS,
	
	ROMSET_ROMS_PATH,
	ROMSET_MOVE_UNKNOWN_FILES,
	ROMSET_UNKNOWN_PATH,
	
	SIZE_TITLE,
	
	NONE
	;

	private static final ResourceBundle res = ResourceBundle.getBundle("jack.rm.i18n.Strings", Locale.ENGLISH);
	
	public String text()
	{
		return res.getString(this.name());
	}
	
	public String toString() { return text(); }
}
