package jack.rm.data.romset;

import jack.rm.data.rom.Rom;

public interface RomHashFinder
{
  Rom getByCRC32(long crc);
}
