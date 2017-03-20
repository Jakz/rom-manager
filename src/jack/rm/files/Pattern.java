package jack.rm.files;

import com.github.jakz.romlib.data.game.Game;

public abstract class Pattern implements Comparable<Pattern>
{
	public final String code, desc;
	public Pattern(String code, String desc)
	{ 
		this.code = code;
		this.desc = desc;
	}
	
  protected String apply(Pattern.RenamingOptions options, String name, String pattern, String replacement)
  {
    if (replacement != null && !replacement.isEmpty())
      return name.replace(pattern, options.open + replacement + options.close);
    else
      return name.replace(pattern, "");
  }
	
	public abstract String apply(RenamingOptions options, String name, Game rom);
	
	public boolean equals(Object other)
	{
	  return other instanceof Pattern && ((Pattern)other).desc.equals(this.desc);
	}
	
	public int compareTo(Pattern other)
	{
	  return desc.compareTo(other.desc);
	}
	
  public static class RenamingOptions
  {
    public final String open;
    public final String close;
    
    public RenamingOptions(String open, String close) { this.open = open; this.close = close; }
  }
}