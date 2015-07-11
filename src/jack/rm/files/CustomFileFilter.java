package jack.rm.files;

import java.io.File;
import java.io.FileFilter;

public class CustomFileFilter implements FileFilter
{
	String[] exts;
	
	public CustomFileFilter(String[] exts)
	{
		this.exts = exts;
	}
	
	public boolean accept(File file)
	{
		String name = file.getName();
		
		if (name.charAt(0) == '.')
			return false;
		
		if (file.isDirectory() || name.endsWith("zip"))
			return true;
		
		for (String s : exts)
		{
			if (name.endsWith(s))
				return true;
		}

		return true;
	}
}