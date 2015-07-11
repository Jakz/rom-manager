package jack.rm;

public abstract class LongTask extends Thread
{
	boolean canceled;
	
	LongTask()
	{
		super();
		
		canceled = false;
	}
	
	protected boolean isCanceled()
	{
		return canceled;
	}
	
	public abstract void executeTask();
	
	@Override
  public void run()
	{
		executeTask();
	}
}
