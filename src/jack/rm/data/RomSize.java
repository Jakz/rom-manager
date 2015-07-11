package jack.rm.data;

import java.util.*;

public class RomSize implements Comparable<RomSize>
{
	public final static int MEGABYTE = 1048576;
	public final static int MEGABIT = MEGABYTE/8;
	
	public final static int KBYTE = 1024;
	public final static int KBIT = KBYTE/8;
	
	public final static String[] shortSizesBit = {"Kb", "Mb", "Gb"};
	public final static String[] longSizesBit = {" Kbit", " Mbit", " Gbit"};
	
	public final static Map<Integer, RomSize> mapping = new TreeMap<Integer, RomSize>();
	
	public static RomSize forBytes(int size)
	{
		if (size%KBYTE != 0)
			size -= size%KBYTE;
		
		RomSize m = mapping.get(size);
		
		if (m == null)
		{
			m = new RomSize(size);
			mapping.put(size, m);
		}
		
		return m;
	}

	public final int bytes;
	
	RomSize(int bytes)
	{
		this.bytes = bytes;
	}
	
	public int bytes()
	{
		return bytes;
	}
	
	public String mbytesAsString()
	{
		int mbits = bytes/MEGABYTE;
		
		if (mbits < 1)
			return bytes/KBYTE+" KByte";
		else
		{
			if (bytes%MEGABYTE == 0)
				return Integer.toString(bytes/MEGABYTE)+ " MByte";
			else
				return String.format("%.2f", bytes/(float)MEGABYTE) +" MByte";
		}
	}
	
	private String bitesAsString(String[] sss)
	{
		int mbits = bytes/MEGABIT;
		
		if (mbits < 1)
			return bytes/KBIT+sss[0];
		else
		{
			if (bytes%MEGABIT == 0)
				return Integer.toString(bytes/MEGABIT)+ sss[1];
			else
				return String.format("%.2f", bytes/(float)MEGABIT) + sss[1];
		}
	}
	
	public String bitesAsStringShort()
	{
		return bitesAsString(shortSizesBit);
	}
	
	public String bitesAsStringLong()
	{
		return bitesAsString(longSizesBit);
	}
		
	@Override
  public String toString()
	{
		return bitesAsStringLong();
	}
	
	@Override
  public int compareTo(RomSize s)
	{
		return bytes < s.bytes ? -1 : (bytes == s.bytes ? 0 : 1);
	}
}
