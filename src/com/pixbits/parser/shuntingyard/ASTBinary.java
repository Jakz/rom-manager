package com.pixbits.parser.shuntingyard;

public class ASTBinary implements ASTNode
{
  public final ASTNode left, right;
  public final Operator operator;
  
  ASTBinary(Operator operator, ASTNode left, ASTNode right)
  {
    this.operator = operator;
    this.left = left;
    this.right = right;
  }
  
  @Override public void accept(Visitor visitor)
  {
    visitor.enterNode(this);
    visitor.visitNode(this);
    left.accept(visitor);
    right.accept(visitor);
    visitor.exitNode(this);
  }
  
  public String toString() { return "ASTBinary("+operator.mnemonic+")"; }

}
