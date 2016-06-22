package com.pixbits.parser.shuntingyard;

public class ASTValue implements ASTNode
{
  public final String value;
  
  ASTValue(String value)
  {
    this.value = value;
  }
  
  @Override public void accept(Visitor visitor)
  {
    visitor.enterNode(this);
    visitor.visitNode(this);
    visitor.exitNode(this);
  }
  
  public String toString() { return "ASTValue("+value+")"; }
}
