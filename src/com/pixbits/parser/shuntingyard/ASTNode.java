package com.pixbits.parser.shuntingyard;

@FunctionalInterface
public interface ASTNode
{
  public void accept(Visitor visitor);
}
