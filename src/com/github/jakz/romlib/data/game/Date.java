package com.github.jakz.romlib.data.game;

public class Date
{
  private final int year;
  private final int month;
  private final int day;
  
  public Date(int year, int month, int day)
  {
    this.year = year;
    this.month = month;
    this.day = day;
  }
  
  public Date(int year, int month)
  {
    this(year, month, -1);
  }
  
  public Date(int year)
  {
    this(year, -1, -1);
  }
}
