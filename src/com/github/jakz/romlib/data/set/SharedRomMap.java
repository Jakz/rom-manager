package com.github.jakz.romlib.data.set;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.jakz.romlib.data.game.Game;
import com.github.jakz.romlib.data.game.Rom;
import com.pixbits.lib.lang.Pair;

public class SharedRomMap
{
  private class RomKey
  {
    private final Rom rom;
    
    public RomKey(Rom rom)
    {
      this.rom = rom;
    }
    
    @Override
    public int hashCode()
    {
      return Objects.hash(rom.size, rom.crc32);
    }
    
    @Override
    public boolean equals(Object other)
    {
      return ((RomKey)other).rom.isEquivalent(rom);
    }
  }
  
  private final Map<RomKey, Set<Pair<Rom,Game>>> sharedRoms;
  private boolean hasAnySharedRom;
  
  public SharedRomMap(final Game[] games)
  {
    sharedRoms = new HashMap<>();
    compute(games);
  }
  
  private void compute(final Game[] games)
  {
    // TODO: streams? parallelize?
    for (Game game : games)
    {
      for (Rom rom : game)
      {
        sharedRoms.compute(new RomKey(rom), (r, s) -> {
          if (s == null)
            return new HashSet<>(Collections.singleton(new Pair<>(rom, game)));
          else
          {
            s.add(new Pair<>(rom, game));
            return s;
          }
        });
      }
    }
    
    /* if all roms have just one game then this info is useless and we can discard it */
    if (sharedRoms.entrySet().stream().allMatch(e -> e.getValue().size() == 1))
      sharedRoms.clear();
    
    hasAnySharedRom = !sharedRoms.isEmpty();
  }
  
  public Stream<Set<Pair<Rom, Game>>> stream()
  {
    return sharedRoms.values().stream();
  }
  
  public boolean hasAnySharedRom()
  {
    return hasAnySharedRom;
  }
  
  public Set<Pair<Rom,Game>> gamesForRom(final Rom rom)
  {
    return sharedRoms.getOrDefault(new RomKey(rom), Collections.singleton(new Pair<>(rom, rom.game())));
  }
  
  
}
