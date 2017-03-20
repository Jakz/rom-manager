package com.github.jakz.romlib.data.game;

public interface GameSave<T extends GameSave.Type>
{
	public static interface Type { }
  
	
	public T getType();
}
