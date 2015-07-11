package jack.rm.files;

import java.io.File;
import java.io.FileFilter;

public class AllFileFilter implements FileFilter
{
	public AllFileFilter()
	{
	}
	
	public boolean accept(File file)
	{
		return true;
	}
}