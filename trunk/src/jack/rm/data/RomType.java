package jack.rm.data;

public enum RomType
{
	GBA("gba"),
	ZIP("zip");
	
	public final String ext;
	
	RomType(String ext)
	{
		this.ext = ext;
	}
}
