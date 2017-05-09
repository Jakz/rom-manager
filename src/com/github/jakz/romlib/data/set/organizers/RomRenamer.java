package com.github.jakz.romlib.data.set.organizers;

import com.github.jakz.romlib.data.game.Rom;

@FunctionalInterface
public interface RomRenamer
{
  String getNameForRom(Rom rom);
}
