package com.pixbits.parser.shuntingyard;

public interface Visitor
{
  void enterNode(ASTNode node);
  void exitNode(ASTNode node);
  void visitNode(ASTNode node);
  
}
