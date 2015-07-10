package jack.rm.data;

public enum RomType
{
	BIN(""),
	ZIP("zip")
	
	;
	
	public final String ext;
	
	RomType(String ext)
	{
		this.ext = ext;
	}
}
