package jack.rm.data;

import java.text.DecimalFormat;
import java.util.*;

public class Renamer
{
	public final static ArrayList<Pattern> patterns = new ArrayList<Pattern>();
	
	private static DecimalFormat format;
	
	public static String renamingPattern;
	private static boolean renameInZipToo;
	
	static
	{
		format = new DecimalFormat();
		format.applyPattern("0000");
		renamingPattern = "%n - %t [%S]";
		renameInZipToo = false;
	}
	
	public static String formatNumber(int index)
	{
		return format.format(index);
	}
	
	public static boolean isCorrectlyNamed(String name, Rom rom)
	{
		return name.equals(getCorrectName(rom));
	}
	
	public static String getCorrectName(Rom rom)
	{
		String temp = new String(renamingPattern);
		
		for (Pattern p : patterns)
			temp = p.apply(temp, rom);
		
		return temp;
	}
	
	public abstract static class Pattern {
		public final String code, desc;
		public Pattern(String code, String desc) { 
			this.code = code;
			this.desc = desc;
			patterns.add(this);
		}
		
		public abstract String apply(String name, Rom rom);
	}
	
	static
	{
		new NumberPattern();
		new TitlePattern();
		new PublisherPattern();
		new MegabyteSizePattern();
		new MegabitSizePattern();
		new FullLocationPattern();
		new ShortLocationPattern();
		new TinyLocationPattern();
	}
	
	static class NumberPattern extends Pattern {
		NumberPattern() { super("%n", "Release number in format 1234"); }
		public String apply(String name, Rom rom) { return name.replace(code,format.format(rom.number)); }
	}
	
	static class TitlePattern extends Pattern {
		TitlePattern() { super("%t", "Game title"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.title); }
	}
	
	static class PublisherPattern extends Pattern {
		PublisherPattern() { super("%c", "Publisher"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.publisher); }
	}
	
	static class MegabyteSizePattern extends Pattern {
		MegabyteSizePattern() { super("%s", "Size of the game dump in megabytes"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.size.mbytesAsString()); }
	}
	
	static class MegabitSizePattern extends Pattern {
		MegabitSizePattern() { super("%S", "Size of the game dump in megabits"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.size.bitesAsStringShort()); }
	}
	
	static class FullLocationPattern extends Pattern {
		FullLocationPattern() { super("%L", "Full location name"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.location.fullName); }
	}
	
	static class ShortLocationPattern extends Pattern {
		ShortLocationPattern() { super("%a", "Short location name"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.location.shortName); }
	}
	
	static class TinyLocationPattern extends Pattern {
		TinyLocationPattern() { super("%l", "Tiny location name"); }
		public String apply(String name, Rom rom) { return name.replace(code,rom.location.tinyName); }
	}
}
