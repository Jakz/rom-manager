package com.github.jakz.romlib.data.game;

public interface GameID<T>
{
  public static class Numeric implements GameID<Integer>
  {
    public final int value;
    public Numeric(int value) { this.value = value; }
  }
  
  public static class Textual implements GameID<String>
  {
    public final String value;
    public Textual(String value) { this.value = value; }
  }
  
  public static class CRC implements GameID<Long>
  {
    public final long value;
    public CRC(long value) { this.value = value; }
    
    public boolean equals(Object o) { return o instanceof CRC && ((CRC)o).value == value; }
    public int hashCode() { return Long.hashCode(value); }
  }
}
