package jack.rm.data;

public interface RomSave<T extends RomSave.Type>
{
	public static interface Type { }
  
	
	public T getType();
}
