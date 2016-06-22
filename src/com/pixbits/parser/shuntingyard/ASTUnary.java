package com.pixbits.parser.shuntingyard;

public class ASTUnary implements ASTNode
{
  public final ASTNode inner;
  public final Operator operator;
  
  ASTUnary(Operator operator, ASTNode inner)
  {
    this.operator = operator;
    this.inner = inner;
  }
  
  @Override public void accept(Visitor visitor)
  {
    visitor.enterNode(this);
    visitor.visitNode(this);
    inner.accept(visitor);
    visitor.exitNode(this);
  }
  
  public String toString() { return "ASTUnary("+operator.mnemonic+")"; }

}
