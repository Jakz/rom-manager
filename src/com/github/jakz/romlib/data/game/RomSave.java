package com.github.jakz.romlib.data.game;

public interface RomSave<T extends RomSave.Type>
{
	public static interface Type { }
  
	
	public T getType();
}
