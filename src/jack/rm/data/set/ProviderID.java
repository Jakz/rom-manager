package jack.rm.data.set;

public enum ProviderID
{
	ADVANSCENE("as","AdvanScene"),
	OFFLINELIST("ol","OfflineList"),
	NOINTRO("ni","NoIntro");
	
	public final String tag;
	public final String name;
	
	ProviderID(String tag, String name)
	{
		this.tag = tag;
		this.name = name;
	}
}
