package com.github.jakz.romlib.data.game;

public interface RomID<T>
{
  public static class Numeric implements RomID<Integer>
  {
    public final int value;
    public Numeric(int value) { this.value = value; }
  }
  
  public static class Textual implements RomID<String>
  {
    public final String value;
    public Textual(String value) { this.value = value; }
  }
  
  public static class CRC implements RomID<Long>
  {
    public final long value;
    public CRC(long value) { this.value = value; }
  }
}
