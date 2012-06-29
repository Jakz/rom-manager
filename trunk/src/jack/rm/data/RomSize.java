package jack.rm.data;

import java.util.*;

public enum RomSize
{
	MBIT1L(1),
	MBIT1(131072),
	MBIT2(262144),
	MBIT4(524288),
	MBIT8(1048576),
	MBIT16(2097152),
	MBIT32(4194304),
	MBIT64(8388608),
	MBIT128(16777216),
	MBIT256(33554432),
	MBIT512(67108864),
	MBIT1024(134217728),
	MBIT2048(268435456),
	MBIT4096(536870912)
	
	;
	
	public final static int MEGABYTE = 1048576;
	public final static int MEGABIT = MEGABYTE/8;
	
	public final static Map<Integer, RomSize> mapping = new HashMap<Integer, RomSize>();
	
	static
	{
		for (RomSize s : values())
			mapping.put(s.bytes, s);
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
		if (bytes == 1) return "<1";
		
		if (bytes%MEGABYTE == 0)
			return Integer.toString(bytes/MEGABYTE);
		else
			return Float.toString(bytes/(float)MEGABYTE);
	}
	
	public String mbitesAsString()
	{
		if (bytes == 1) return "<1";
		
		if (bytes%MEGABIT == 0)
			return Integer.toString(bytes/MEGABIT);
		else
			return Float.toString(bytes/(float)MEGABIT);
	}
	
	public static RomSize forBytes(int size)
	{
		RomSize m = mapping.get(size);
		
		if (m != null)
			return m;
		
		return MBIT1L;
		/*if (m == null)
		{
			if (size < MBIT1.bytes)
				return MBIT1L;
		}*/

		//return m;
	}
	
	public static RomSize forName(String name)
	{
		for (RomSize s : mapping.values())
			if (s.toString().equals(name))
				return s;
		
		return null;
	}
	
	public String toString()
	{
		return mbitesAsString()+" Mbits";
	}
	
}
