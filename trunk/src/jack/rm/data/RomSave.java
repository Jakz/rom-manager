package jack.rm.data;

public enum RomSave
{
	EEPROM("EEPROM"),
	FLASH("FLASH"),
	SRAM("SRAM"),
	NONE("NONE")
	
	;
	
	public final String name;
	
	RomSave(String name)
	{
		this.name = name;
	}
}
