package com.github.jakz.romlib.data.game;

public interface GameSave<T extends GameSave.Type>
{
	public static interface Type { }
  
	
	public T getType();
	
	public static final GameSave.Type NULL_TYPE = new Type() { };
	public static final GameSave<GameSave.Type> NULL = new GameSave<GameSave.Type>() {
    @Override
    public Type getType()
    {
      return NULL_TYPE;
    } 
  };
}
