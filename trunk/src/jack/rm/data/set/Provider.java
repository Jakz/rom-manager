package jack.rm.data.set;

public enum Provider
{
	ADVANSCENE("as","AdvanScene"),
	OFFLINELIST("ol","OfflineList"),
	NOINTRO("ni","NoIntro");
	
	public final String tag;
	public final String name;
	
	Provider(String tag, String name)
	{
		this.tag = tag;
		this.name = name;
	}
}
