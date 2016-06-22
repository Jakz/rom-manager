package com.pixbits.parser.shuntingyard;

public class PrintVisitor implements Visitor
{
  private StringBuilder indent;
  
  PrintVisitor()
  {
    indent = new StringBuilder();
  }
  
  
  @Override
  public void enterNode(ASTNode node)
  {
    indent.append("  ");
  }

  @Override
  public void exitNode(ASTNode node)
  {
    indent.delete(0, 2);

  }

  @Override
  public void visitNode(ASTNode node)
  {
    if (node instanceof ASTValue)
      System.out.printf("%s Value: %s\n", indent, ((ASTValue)node).value);
    else if (node instanceof ASTUnary)
      System.out.printf("%s Unary: %s\n", indent, ((ASTUnary)node).operator.mnemonic);
    else if (node instanceof ASTBinary)
      System.out.printf("%s Binary: %s\n", indent, ((ASTBinary)node).operator.mnemonic);
  }

}
