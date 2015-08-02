package jack.rm.files;

import java.io.IOException;
import java.util.Optional;

import com.pixbits.io.BinaryBuffer;
import com.pixbits.io.BufferPosition;

import jack.rm.data.console.GBA;
import jack.rm.data.rom.Version;

public class GBASavePatcherGBATA
{
  static private byte[] toBytes(String string) { return javax.xml.bind.DatatypeConverter.parseHexBinary(string); }
  
  static private PatchEntry[] buildPatch(String... strings)
  {
    PatchEntry[] entries = new PatchEntry[strings.length / 2];
    for (int i = 0; i < entries.length; ++i)
      entries[i] = new PatchEntry(toBytes(strings[2*i]), toBytes(strings[2*i + 1]));
    
    return entries;
  }
  
  private static class PatchEntry
  {
    byte[] fromBlock;
    byte[] toBlock;
    
    PatchEntry(byte[] fromBlock, byte[] toBlock)
    {
      this.fromBlock = fromBlock;
      this.toBlock = toBlock;
    }
    
    void patch(BinaryBuffer buffer) throws IOException
    {
      buffer.replace(fromBlock, toBlock);
    }
  }
  
  private static final PatchEntry[] Flash_v102 = buildPatch(
    "aa211970054a55211170b0211970e0210905087070475555000eaa2a000e30b591b0684600f0f3f86d460135064aaa20",
    "80210902092212069f4411800349c302c91811807047feffff010000000030b591b0684600f0f3f86d460135064aaa2000000549552000009020000010a9034a101c08e000005555000eaa2a000e204e000008880138088008880028f9d10c48132013200006040ce0200005622062200006000e04430749aa200000074a55200000f02000000000",
    "1449aa240c70134b55221a70802008700c701a7010200870",
    "0e210906ff248022134b5202013a8c54fcd1000000000000",
    "aa250d70134b55221a70802008700d701a7030202070",
    "ff25082200005202013aa554fcd10000000000000000",
    "2270094b55221a70a0222270",
    "0000094b55220000a0220000"
  );

  
  private static final PatchEntry[] Flash_v103 = buildPatch(
    "054baa211970054a55211170b0211970e0210905087070475555000eaa2a000e30b591b0684600f0f3f86d460135064aaa2010700549552008709020107010a9034a101c08e000005555000eaa2a000e204e000008880138088008880028f9d10c48",
    "054b80210902092212069f4411800349c302c91811807047feffff010000000030b591b0684600f0f3f86d460135064aaa2000000549552000009020000010a9034a101c08e000005555000eaa2a000e204e000008880138088008880028f9d10c48132013200006040ce0200005622062200006000e04430749aa200000074a55200000f02000000000",
    "1449aa240c70134b55221a70802008700c701a7010200870",
    "0e210906ff248022134b5202013a8c54fcd1000000000000",
    "aa250d70144b55221a70802008700d701a7030202070",
    "ff25082200005202013aa554fcd10000000000000000",
    "10700b4955200870a0201070",
    "00000b4955200000a0200000",
    "2270094b55221a70a0222270",
    "0000094b55220000a0220000"    
  );
  
  private static final PatchEntry[] Flash_v130_v131_v133 = buildPatch(
    "f0b5a0b00d1c161c1f1c03041c0c0f4a10880f4908400321084310800d480068016880208002",
    "70b5a0b000034018e0210905091808781070013b01320131002bf8d1002020b070bc02bc0847",
    "fff788fd0004030c",
    "1b231b0232200343",
    "70b590b0154d2988",
    "00b5002002bc0847",
    "70b5464640b490b0",
    "00b5002002bc0847",
    "f0b590b00f1c0004040c034800684089844205d3014841e0",
    "7cb590b000030a1ce0210905091801231b0310780870013b01320131002bf8d1002010b07cbc02bc0847"
  );
  
  // EEPROM_V111
  
  private static final PatchEntry[] Eeprom_v120_v121_v122 = buildPatch(
    "a2b00d1c0004030c034800688088834205d3014848e0",
    "00040a1c400be021090541180731002308781070013301320139072bf8d9002070bc02bc0847",
    "30b5a9b00d1c0004040c034800688088844205d3014859e0",
    "70b500040a1c400be021090541180731002310780870013301320139072bf8d9002070bc02bc0847"
  );
  
  /*private static final PatchEntry[] Eeprom_v124 = buildPatch(
    "a2b00d1c0004030c034800688088834205d3014848e0",
    "00040a1c400be021090541180731002308781070013301320139072bf8d9002070bc02bc0847",
    "f0b5acb00d1c0004010c1206170e034800688088814205d3",
    "70b500040a1c400be021090541180731002310780870013301320139072bf8d9002070bc02bc0847"
  );*/
  
  private static final PatchEntry[] Eeprom_v124 = buildPatch(
      "A2B00D1C0004030C034800688088834205D3014844E0D0580003FF8000002248061C0068017A",
      "00040a1c400be021090541180731002308781070013301320139072bf8d9002070bc02bc0847",
      "F0B5ACB00D1C0004010C1206170E034800688088814205D301489DE0D0580003FF8000000F480068",
      "70b500040a1c400be021090541180731002310780870013301320139072bf8d9002070bc02bc0847"
    );
  
  private static final PatchEntry[] Eeprom_v126 = buildPatch(
    "a2b00d1c0004030c034800688088834205d3014848e0",
    "00040a1c400be021090541180731002308781070013301320139072bf8d9002070bc02bc0847",
    "f0b5474680b4acb00e1c0004050c1206120e904603480068",
    "70b500040a1c400be021090541180731002310780870013301320139072bf8d9002070bc02bc0847"
  );
  
  private static final PatchEntry[] Flash_v120_v121 = buildPatch(
    "90b593b06f46391d081c00f0",
    "00b53d2000021f21084302bc0847",
    "80b594b06f46391c0880381c01880f2904d9014856e00000ff800000234823490a8823",
    "7cb50007000ce0210905091801231b03ff200870013b0131002bfad100207cbc02bc0847",
    "80b594b06f467960391c0880381c01880f2903d9004873e0ff800000381c0188081cfff721fe391c0c31",
    "7cb590b000030a1ce0210905091801231b0310780870013b01320131002bf8d1002010b07cbc08bc0847"
  );
  
  private static final PatchEntry[] Flash_v123_v124 = buildPatch(
    "fff7aaff0004030c",
    "1b231b0232200343",
    "70b590b0154d",
    "00207047154d",
    "70b5464640b4",
    "0020704740b4",
    "f0b590b00f1c0004040c0f2c04d9014840e00000ff800000201cfff7d7fe0004050c002d35d1",
    "70b500030a1ce0210905411801231b0310780870013b01320131002bf8d1002070bc02bc0847" 
  );
  
  private static final PatchEntry[] Flash_v125_v126 = buildPatch(
    "fff7aaff0004030c",
    "1b231b0232200343",
    "000370b590b0154d",
    "000300207047154d",
    "000370b5464640b4",
    "00030020704740b4",
    "f0b590b00f1c0004040c0f2c04d9014840e00000ff800000201cfff7d7fe0004050c002d35d1",
    "70b500030a1ce0210905411801231b0310780870013b01320131002bf8d1002070bc02bc0847"   
  );
      

  private static void patchEeprom111(BinaryBuffer buffer) throws IOException
  {
    byte[] firstBlock = toBytes("27e0d02000050188");
    byte[] firstBlockReplacement = toBytes("27e0e02000050188");
    
    buffer.replace(firstBlock, firstBlockReplacement);
    
    byte[] payload1from = toBytes("0e48396801600e48");
    byte[] payload1to = toBytes("0048004700000008");
    int payload1addressOffset = 4;
    
    byte[] payload2 = toBytes("39682748814223d0891c0888012802d12448786033e000230022891c10b40124086820405b000343891c521c062af7d110bc3960db01022000021b180e2000061b187b60391c083108880938088016e015490023002210b40124086820405b000343891c521c062af7d110bcdb01022000021b180e2000061b18083b3b600b48396801600a48796801600a48391c08310a88802109060a4302600748004700000000000d0000000e0400000ed4000004d8000004dc00000400000008");
    int payload2addressOffset = 184; 
    
    long payload2Position = buffer.length() - 1;
    byte value = buffer.read(payload2Position);
    
    // find first unused byte starting from end
    while (value == 0x00 || value == (byte)0xff)
      value = buffer.read(--payload2Position);
    
    ++payload2Position;
    
    // adjust position to be aligned to 16 bytes
    payload2Position += 16 - (payload2Position % 16);
    
    // add 256 bytes if there is not enough room available
    if (buffer.length() - payload2Position < payload2.length)
    {
      buffer.resize(buffer.length() + 256);
    }
    
    buffer.replace(payload2, (int)payload2Position);
    Optional<BufferPosition> payload1Position = buffer.replace(payload1from, payload1to);
    
    buffer.writeU24((int)payload2Position + 1, payload1Position.get().get() + payload1addressOffset);
    buffer.writeU24(payload1Position.get().get() + 1 + 32, (int)payload2Position + payload2addressOffset);
  }
  
  public static void patch(GBA.Save save, BinaryBuffer buffer)
  {
    GBA.Save.Type type = save.getType();
    Version version = save.getVersion();
    PatchEntry[] patch = null;
   
    if (type == GBA.Save.Type.SRAM)
      return; // no action needed
    else if (type == GBA.Save.Type.FLASH)
    {
      switch ((GBA.Save.Flash)version)
      {
        case v102: patch = Flash_v102; break;
        case v103: patch = Flash_v103; break;
        case v120:
        case v121:
          patch = Flash_v120_v121; break;
        case v123:
        case v124:
          patch = Flash_v123_v124; break;
        case v125:
        case v126:
          patch = Flash_v125_v126; break;
        case v130:
        case v131:
        case v133:
          patch = Flash_v130_v131_v133; break;
      }
    }   
    else if (type == GBA.Save.Type.EEPROM)
    {
      switch ((GBA.Save.EEPROM)version)
      {
        case v120:
        case v121:
        case v122:
          patch = Eeprom_v120_v121_v122; break;
        case v124:
          patch = Eeprom_v124; break;
        case v126:
          patch = Eeprom_v126; break;
          
        case v111: break; // handled after
      }
    }
    
    try
    {
      if (type == GBA.Save.Type.EEPROM && version == GBA.Save.EEPROM.v111)
        patchEeprom111(buffer);
      else if (patch != null)
      {
        for (PatchEntry entry : patch)
          entry.patch(buffer);
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
      
  }
}

/* courtesy of TrolleyDave http://gbatemp.net/threads/reverse-engineering-gba-patching.60168/ 
 
Save Pattern Block for Eeprom_V111
Unpatched block 1 (8 Bytes)
($0e,$48,$39,$68,$01,$60,$0e,$48)
Patch with
($00,$48,$00,$47,$XX,$XX,$XX,$08) (See below for what to fill the XXs with)

Unpatched block 2 (8 Bytes)
($27,$e0,$d0,$20,$00,$05,$01,$88)
Patch with
($27,$e0,$e0,$20,$00,$05,$01,$88)

Patch block 3 (188 Bytes) (goes at the end of the rom data, see notes below)
($39,$68,$27,$48,$81,$42,$23,$d0,$89,$1c,$08,$88,$01,$28,$02,$d1,
$24,$48,$78,$60,$33,$e0,$00,$23,$00,$22,$89,$1c,$10,$b4,$01,$24,
$08,$68,$20,$40,$5b,$00,$03,$43,$89,$1c,$52,$1c,$06,$2a,$f7,$d1,
$10,$bc,$39,$60,$db,$01,$02,$20,$00,$02,$1b,$18,$0e,$20,$00,$06,
$1b,$18,$7b,$60,$39,$1c,$08,$31,$08,$88,$09,$38,$08,$80,$16,$e0,
$15,$49,$00,$23,$00,$22,$10,$b4,$01,$24,$08,$68,$20,$40,$5b,$00,
$03,$43,$89,$1c,$52,$1c,$06,$2a,$f7,$d1,$10,$bc,$db,$01,$02,$20,
$00,$02,$1b,$18,$0e,$20,$00,$06,$1b,$18,$08,$3b,$3b,$60,$0b,$48,
$39,$68,$01,$60,$0a,$48,$79,$68,$01,$60,$0a,$48,$39,$1c,$08,$31,
$0a,$88,$80,$21,$09,$06,$0a,$43,$02,$60,$07,$48,$00,$47,$00,$00,
$00,$00,$00,$0d,$00,$00,$00,$0e,$04,$00,$00,$0e,$d4,$00,$00,$04,
$d8,$00,$00,$04,$dc,$00,$00,$04,$XX,$XX,$XX,$08)
 
 Notes
This ones a little trickier than the rest of them. I've managed to compact all the other save types into one patching routine but for this I've had to use a completely different patching routine. As you can see from the patch blocks there's a couple of non-standard bytes in Patch block 1 and Patch block 3. Patch block 2 is a simple search and patch routine, I'll explain about blocks 1 and 2 below. Before you begin your patching for this save type you have to calculate where the end of the actual rom data is as Patch block 3 needs to bea appended to the empty space after. Unfortunately though it's not as simple as jus fnding the first free byte, the patch has to begin in the first empty byte divisible by 16 (no remainder, in Pascal it's EOFMarker MOD 16 = 0). Make sure that this number is kept in a 32-bit number as your going to need the first 3 bytes of it! [​IMG]

Patch block 1
The unpatched search block 1 is standard between all roms so the same kind of search routines you used for all the others will do. Before patching the data though you need to fill in bytes 5,6 and 7 of the patch block. This data needs to be the first 3 bytes of the 32 bit number that you stored the EOF position in. The number that you should be patching in is the EOF marker position you stored earlier plus 1. So if the patch starts at file position 3000000 you need to apply the number 300001.

The way I did it in Pascal (there's probably an easier way) is to move the Integer into an 4 byte array (call it TmpArray for ease) and then copy the relevant bytes from that array into the patch data. So if the patch data is stored as an 8 byte array called PatchArray the code would simply be PatchArray[5] := TmpArray[1] and PatchArray[6] := TmpArray[2] and so on. Then it's just a simple matter of pasting the modified patch data in there. Keep a marker of the byte position of the start of this data as you'll need it to fill in the relevant data in patch block 3.

Patch block 3
Patch block 3 is actually pretty easy as there's no data to search for to overwrite with the patch, it's simply added in the empty space at the end of the rom. It has to be placed in the first position after the end of the rom data that's divisible by 16 (it has to be 16 byte aligned) or the rom won't be able to access the save data.

You need to modify bytes 185, 186 and 187 in the patch block. Again it's another offset and it's a 3 byte length number (a 24-bit number) the same as in patch block 1. Alter the data in this patch block exactly the same way that you altered the data in patch block 1. The number you need to fill bytes 185-187 with is the offset of patch 1 + 32 bytes. So if patch 1 is store at file offset 30000 the number you need to fill bytes 185-187 with would be 30032. Again the way I did it was just to move the 32 bit number I had the file offset + 32 stored in into an array of bytes and then copied the relevant bytes into the patch data. Once you've filled bytes 185-187 with the correct data you can just patch the data directly into the file.

Just as a side note, if the file is already trimmed by a trimmer the easiest thing to do would be to append 512 bytes of blank data to the end of the rom and then run your patching routine like normal.

And that folks is Eeprom_V111 patching! I know I probably haven't explained it too well but if you need any clarification on anything or you want to have a look at my patching routines then just PM me.
 
 
 
 */
