package jack.rm.log;

import java.util.ArrayList;
import java.util.List;

import jack.rm.Main;

public class Log
{
  private static final List<LogMessage> messages = new ArrayList<>();
  
  public static void log(LogMessage msg) { 
    messages.add(msg); 
    if (Main.mainFrame != null)
      Main.mainFrame.cardConsole.populate();
  }
  
  public static void log(LogType type, LogSource source, LogTarget target, String message) { log(new LogMessage(type, source, target, message)); }
  public static void log(LogType type, LogSource source, String message) { log(new LogMessage(type, source, new LogTarget.None(), message)); }
  
  public static void wipe() { messages.clear(); }
  public static List<LogMessage> get() { return messages; }

}
