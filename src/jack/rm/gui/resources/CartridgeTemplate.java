package jack.rm.gui.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.github.jakz.romlib.data.platforms.Platform;
import com.github.jakz.romlib.ui.carts.CartridgeLayout;

/** Catalog of cartridge/media shells shipped with the application. */
public final class CartridgeTemplate
{
  private static final String RESOURCE_ROOT = "/jack/rm/gui/resources/cartridges/";
  private static final List<CartridgeTemplate> VALUES = new ArrayList<>();

  private final String id;
  private final String caption;
  private final Set<String> platformTags;
  private CartridgeLayout layout;

  private CartridgeTemplate(String id, String caption, String... platformTags)
  {
    this.id = id;
    this.caption = caption;
    this.platformTags = new LinkedHashSet<>(Arrays.asList(platformTags));
    VALUES.add(this);
  }

  public String id() { return id; }
  public String caption() { return caption; }
  public boolean isRecommendedFor(Platform platform) { return platform != null && platformTags.contains(platform.getTag()); }

  public synchronized CartridgeLayout layout()
  {
    if (layout == null)
    {
      try
      {
        BufferedImage shell = ImageIO.read(CartridgeTemplate.class.getResource(RESOURCE_ROOT + "shells/" + id + ".png"));
        BufferedImage mask = ImageIO.read(CartridgeTemplate.class.getResource(RESOURCE_ROOT + "masks/" + id + ".png"));
        layout = new CartridgeLayout(shell, mask);
      }
      catch (IOException | IllegalArgumentException e)
      {
        throw new RuntimeException("Unable to load cartridge template " + id, e);
      }
    }
    return layout;
  }

  @Override public String toString() { return caption; }

  public static List<CartridgeTemplate> values()
  {
    return Collections.unmodifiableList(VALUES);
  }

  public static Optional<CartridgeTemplate> byId(String id)
  {
    if (id == null)
      return Optional.empty();
    return VALUES.stream().filter(template -> template.id.equals(id)).findFirst();
  }

  public static Optional<CartridgeTemplate> defaultFor(Platform platform)
  {
    return VALUES.stream().filter(template -> template.isRecommendedFor(platform)).findFirst();
  }

  public static List<CartridgeTemplate> recommendedFor(Platform platform)
  {
    return VALUES.stream().filter(template -> template.isRecommendedFor(platform)).collect(Collectors.toList());
  }

  public static final CartridgeTemplate COMPACT_WHITE_CARD = new CartridgeTemplate(
      "compact-white-card", "Compact white card", "ws");
  public static final CartridgeTemplate NINTENDO_64 = new CartridgeTemplate(
      "nintendo-64", "Nintendo 64 style", "n64");
  public static final CartridgeTemplate COMPACT_BLACK_CARD = new CartridgeTemplate(
      "compact-black-card", "Compact black card", "ns");
  public static final CartridgeTemplate GAME_BOY = new CartridgeTemplate(
      "game-boy", "Game Boy style", "gb");
  public static final CartridgeTemplate GAME_BOY_COLOR = new CartridgeTemplate(
      "game-boy-color", "Game Boy Color style", "gbc");
  public static final CartridgeTemplate GAME_BOY_ADVANCE = new CartridgeTemplate(
      "game-boy-advance", "Game Boy Advance style", "gba");
  public static final CartridgeTemplate NES = new CartridgeTemplate(
      "nes", "NES style", "nes");
  public static final CartridgeTemplate SUPER_NINTENDO = new CartridgeTemplate(
      "super-nintendo", "Super Nintendo style", "snes");
  public static final CartridgeTemplate NINTENDO_DS = new CartridgeTemplate(
      "nintendo-ds", "Nintendo DS style", "nds");
  public static final CartridgeTemplate ATARI_2600 = new CartridgeTemplate(
      "atari-2600", "Atari 2600 style", "a2600");
  public static final CartridgeTemplate MEGA_DRIVE = new CartridgeTemplate(
      "mega-drive", "Mega Drive style", "md");
  public static final CartridgeTemplate GAME_GEAR = new CartridgeTemplate(
      "game-gear", "Game Gear style", "gg");
  public static final CartridgeTemplate MASTER_SYSTEM = new CartridgeTemplate(
      "master-system", "Master System style");
  public static final CartridgeTemplate NEO_GEO_POCKET = new CartridgeTemplate(
      "neo-geo-pocket", "Neo Geo Pocket style", "ngp");
  public static final CartridgeTemplate WONDERSWAN = new CartridgeTemplate(
      "wonderswan", "WonderSwan style", "ws");
  public static final CartridgeTemplate ATARI_LYNX = new CartridgeTemplate(
      "atari-lynx", "Atari Lynx style", "lynx");
  public static final CartridgeTemplate NINTENDO_3DS = new CartridgeTemplate(
      "nintendo-3ds", "Nintendo 3DS style", "3ds");
  public static final CartridgeTemplate MINI_OPTICAL_DISC = new CartridgeTemplate(
      "mini-optical-disc", "Mini optical disc", "gc");
  public static final CartridgeTemplate OPTICAL_DISC_SILVER = new CartridgeTemplate(
      "optical-disc-silver", "Silver optical disc", "ps1", "ps2", "pc");
  public static final CartridgeTemplate OPTICAL_DISC_BLUE_RING = new CartridgeTemplate(
      "optical-disc-blue-ring", "Blue-ring optical disc");
  public static final CartridgeTemplate OPTICAL_DISC_REFLECTIVE = new CartridgeTemplate(
      "optical-disc-reflective", "Reflective optical disc");
  public static final CartridgeTemplate OPTICAL_DISC_CLASSIC = new CartridgeTemplate(
      "optical-disc-classic", "Classic optical disc");
  public static final CartridgeTemplate OPTICAL_DISC_MODERN = new CartridgeTemplate(
      "optical-disc-modern", "Modern optical disc");
  public static final CartridgeTemplate OPTICAL_DISC_BLUE = new CartridgeTemplate(
      "optical-disc-blue", "Blue optical disc");
  public static final CartridgeTemplate PSP_UMD = new CartridgeTemplate(
      "psp-umd", "PSP UMD style", "psp");
  public static final CartridgeTemplate OPTICAL_DISC_GREEN = new CartridgeTemplate(
      "optical-disc-green", "Green optical disc");
}
