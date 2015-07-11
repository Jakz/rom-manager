package jack.rm.log;

public class LogMessage
{
  final public LogType type;
  final public LogSource source;
  final public LogTarget target;
  final public String message;
  
  public LogMessage(LogType type, LogSource source, LogTarget target, String message)
  {
    this.type = type;
    this.source = source;
    this.target = target;
    this.message = message;
  }
}
