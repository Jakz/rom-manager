package com.github.jakz.romlib.support.header;

public class Rule
{
  public final int bytesToSkip;
  
  private Rule(int bytesToSkip)
  {
    this.bytesToSkip = bytesToSkip;
  }
  
  public static Rule of(int bytesToSkip)
  {
    return new Rule(bytesToSkip);
  }
}
