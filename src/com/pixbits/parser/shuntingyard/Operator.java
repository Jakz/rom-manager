package com.pixbits.parser.shuntingyard;

public class Operator implements Comparable<Operator>
{
  final String mnemonic;
  final int precedence;
  final boolean rightAssociative;
  final boolean unary;
  
  Operator(String mnemonic, int precedence, boolean unary, boolean rightAssociative)
  {
    this.mnemonic = mnemonic;
    this.precedence = precedence;
    this.rightAssociative = rightAssociative;
    this.unary = unary;
  }
  
  @Override 
  public int compareTo(Operator other) { return precedence - other.precedence; } 
  public boolean isRightAssociative() { return rightAssociative; }
  public boolean isUnary() { return unary; }
  
}
