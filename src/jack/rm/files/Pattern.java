package jack.rm.files;

import java.util.function.Function;

public abstract class Pattern<T> implements Comparable<Pattern<T>>
{
	public final String code, desc;
	public Pattern(String code, String desc)
	{ 
		this.code = code;
		this.desc = desc;
	}
	
	protected String apply(Pattern.RenamingOptions options, String template, String replacement)
	{
	  return apply(options, template, code, replacement);
	}
	
  protected String apply(Pattern.RenamingOptions options, String name, String pattern, String replacement)
  {
    if (replacement != null && !replacement.isEmpty())
      return name.replace(pattern, options.open + replacement + options.close);
    else
      return name.replace(pattern, "");
  }
	
	public abstract String apply(RenamingOptions options, String name, T data);
	
	public boolean equals(Object other)
	{
	  return other instanceof Pattern && ((Pattern<?>)other).desc.equals(this.desc);
	}
	
	public int compareTo(Pattern<T> other)
	{
	  return desc.compareTo(other.desc);
	}
	
  public static class RenamingOptions
  {
    public final String open;
    public final String close;
    
    public RenamingOptions(String open, String close) { this.open = open; this.close = close; }
  }
  
  public static <K> Pattern<K> of(final String code, final String desc, Function<K, String> lambda)
  {
    return new Pattern<K>(code, desc)
    {
      @Override
      public String apply(RenamingOptions options, String template, K data) { return apply(options, template, code, lambda.apply(data)); }
    };
  }
}