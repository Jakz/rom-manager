package com.github.jakz.romlib.data.game;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

public class RomSize implements Comparable<RomSize>
{
	public final static long GIGABYTE = 1 << 30;
	public final static long GIGABIT = GIGABYTE / 8;
  
  public final static long MEGABYTE = 1 << 20;
	public final static long MEGABIT = MEGABYTE / 8;
	
	public final static long KBYTE = 1 << 10;
	public final static long KBIT = KBYTE / 8;
	
	public static enum PrintStyle
	{
	  SHORT,
	  LONG
	};
	
	public static enum PrintUnit
	{
	  BITS,
	  BYTES
	};
	
	public static enum Unit
	{
	  KILOBYTE(1 << 10, "KByte", "KB", null),
	  MEGABYTE(1 << 20, "MByte", "MB", KILOBYTE),
    GIGABYTE(1 << 30, "GByte", "GB", MEGABYTE),
    
    KILOBIT(KILOBYTE.bytes/8, "Kbit", "Kb", null),
    MEGABIT(MEGABYTE.bytes/8, "Mbit", "Mb", KILOBIT),
    GIGABIT(GIGABYTE.bytes/8, "Gbit", "Gb", MEGABIT);
	  
	  public final String shortName;
	  public final String longName;
	  public final long bytes;
	  public final Unit next;
	  
	  Unit(long bytes, String longName, String shortName, Unit next)
	  {
	    this.bytes = bytes;
	    this.longName = longName;
	    this.shortName = shortName;
	    this.next = next;
	  }
	  
	  public String getCaption(PrintStyle style)
	  {
	    return style == PrintStyle.SHORT ? shortName : (" "+longName);
	  }
	  
	}
	
	private final long bytes;
	
	RomSize(long bytes)
	{
		this.bytes = bytes;
	}
	
	@Override public boolean equals(Object other)
	{
	  return (other instanceof RomSize) && bytes == ((RomSize)other).bytes;
	}
	
	public long bytes()
	{
		return bytes;
	}
	
	public RomSize add(RomSize other)
	{
	  return new RomSize(bytes + other.bytes);
	}
	
	public String toString(PrintStyle style, PrintUnit unit)
	{
	  return toString(bytes, style, unit);
	}
	
	public static String toString(long bytes)
	{
	  return toString(bytes, PrintStyle.LONG, PrintUnit.BITS);
	}

	public static String toString(long bytes, PrintStyle style, PrintUnit unit)
	{
	  Unit size = unit == PrintUnit.BITS ? Unit.GIGABIT : Unit.GIGABYTE;
	  
	  if (bytes == 0)
	    return "0 bytes";
	  
	  while (size != null)
	  {
	    long units = bytes / size.bytes;
	    
	    if (units < 1)
	      size = size.next;
	    else
	    {
	      if (bytes % size.bytes == 0)
	        return String.format("%d%s", units, size.getCaption(style));
	      else
	        return String.format("%.2f%s", bytes / (float)size.bytes, size.getCaption(style));
	    }
	  }
	  
	  return "Unknown Size"; 
	}

	@Override
  public String toString()
	{
		return toString(PrintStyle.LONG, PrintUnit.BITS);
	}
	
	@Override
  public int compareTo(RomSize s)
	{
		return Long.compare(this.bytes, s.bytes);
	}
	
	private static long roundToKByte(long size)
	{
    long reminder = size % KBYTE;
    
    if (reminder != 0)
    {
      if (reminder < KBYTE/2)
        size -= reminder;
      else
        size += KBYTE - reminder;
    }
    
    return size;
	}
	
	public static abstract class Set implements Iterable<RomSize>
	{
	  public final RomSize forBytes(long size) { return forBytes(size, true); }
	  abstract RomSize forBytes(long size, boolean addToList);
	  
	  protected RomSize forBytesImpl(long size)
	  {
	    return new RomSize(size);
	  }
	}
	
	public static class NullSet extends Set
	{
	  public RomSize forBytes(long size, boolean addToList)
	  {
	    return forBytesImpl(size);
	  }
	  
	  public Iterator<RomSize> iterator() { return Collections.emptyIterator(); }
	}

	public static class RealSet extends Set
	{
	  private final Map<Long, RomSize> mapping = new TreeMap<>();

	  public RomSize forBytes(long size, boolean addToList)
	  {
	    size = roundToKByte(size);

	    RomSize m = mapping.get(size);
	    
	    if (m == null)
	    {
	      m = new RomSize(size);
	      
	      if (addToList)
	        mapping.put(size, m);
	    }
	    
	    return m;
	  }
	  
	  public Iterator<RomSize> iterator() { return values().iterator(); }
	  public Collection<RomSize> values() { return mapping.values(); }
	}
}
