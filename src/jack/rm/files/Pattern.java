package jack.rm.files;

import jack.rm.data.Rom;

public abstract class Pattern implements Comparable<Pattern>
{
	public final String code, desc;
	public Pattern(String code, String desc)
	{ 
		this.code = code;
		this.desc = desc;
	}
	
	public abstract String apply(String name, Rom rom);
	
	public boolean equals(Object other)
	{
	  return other instanceof Pattern && ((Pattern)other).desc.equals(this.desc);
	}
	
	public int compareTo(Pattern other)
	{
	  return desc.compareTo(other.desc);
	}
}