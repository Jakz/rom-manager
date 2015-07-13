package jack.rm.data;


public class NumberedRom extends Rom
{
  public int number;
  
  public NumberedRom() { }

  public NumberedRom(int number)
  {
    this.number = number;
  }
  
  @Override
  public boolean equals(Object other)
  {
    return other instanceof Rom && ((NumberedRom)other).number == number;
  }
  
  @Override
  public int compareTo(Rom rom)
  {
    return number - ((NumberedRom)rom).number;
  }
}
