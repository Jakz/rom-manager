# Cartridge and media template workflow

## Result

The generated sheets have been converted into 26 independently selectable cartridge/media templates. Each template has two runtime resources:

- `shells/<id>.png`: the isolated physical shell on a transparent 640×440 canvas;
- `masks/<id>.png`: a grayscale map of the printable label area.

The original seven generated sheets are preserved under `docs/ai/cartridge-sources/` with stable names. They are outside `src`, so they are not copied into the application JAR.

The generated overview is [cartridge-templates-preview.png](cartridge-templates-preview.png). Its colored stripes are test artwork demonstrating the mapped label regions; they are not runtime assets.

## Runtime model

`AssetKind.CARTRIDGE` represents the **downloaded label artwork**, not the physical plastic shell. This separation allows one label file to be rendered with a different shell without modifying or duplicating the downloaded asset.

The render sequence is:

1. Load the selected transparent shell.
2. Scale downloaded label artwork to cover the mapped label bounds.
3. Clip the artwork with the template's grayscale mask.
4. Composite the clipped label over the blank shell.
5. Scale the finished cartridge into the dataset's configured cartridge display size.

When **Render Cartridge Shell** is enabled, the cartridge view uses this composition path. If label artwork is missing, the blank selected shell is still displayed and its tooltip says that the label is missing. Platforms without an automatic mapping use the generic compact white shell. If shell rendering is disabled, available label artwork is shown directly.

## Per-dataset controls

The main **View > Assets** menu provides fixed-order visibility toggles for:

- Box Art;
- Gameplay Screenshot;
- Title Screen;
- Cartridge Label.

It also provides the **Render Cartridge Shell** checkbox. Enabling it automatically makes Cartridge Label visible.

The controls apply immediately and are stored per dataset. Asset reordering is not supported yet.

The game information panel's **Assets** menu also provides the same visibility controls together with the cartridge-specific presentation setting:

- `Cartridge/media style` with automatic platform selection and all 26 explicit templates.

Both the shell-rendering choice and selected template ID are stored in the existing per-dataset `settings.json`. Shell rendering defaults to enabled when the setting is absent.

Automatic mappings currently cover:

| Platform | Default template |
| --- | --- |
| Atari 2600 | Atari 2600 style |
| Atari Lynx | Atari Lynx style |
| Game Boy | Game Boy style |
| Game Boy Color | Game Boy Color style |
| Game Boy Advance | Game Boy Advance style |
| GameCube | Mini optical disc |
| Game Gear | Game Gear style |
| Mega Drive / Genesis | Mega Drive style |
| NES | NES style |
| Nintendo 64 | Nintendo 64 style |
| Nintendo DS | Nintendo DS style |
| Nintendo 3DS | Nintendo 3DS style |
| Nintendo Switch | Compact black card |
| Neo Geo Pocket | Neo Geo Pocket style |
| PlayStation / PlayStation 2 | Silver optical disc |
| PSP | PSP UMD style |
| PC | Silver optical disc |
| Super Nintendo | Super Nintendo style |
| WonderSwan | Compact white card (WonderSwan style is also recommended) |

Platforms without a default can still use any template explicitly.

## Asset-source behavior

`DATA_FETCHER` plugins were already non-mutually-exclusive, but the game information panel previously selected an arbitrary single enabled plugin. When multiple asset sources are enabled, **Download Assets** now opens a source menu and lets the user choose which provider to search.

Any current or future fetcher can provide `AssetKind.CARTRIDGE` artwork. The shell renderer will pick it up through the ordinary `AssetData` path. The Libretro thumbnail source currently exposes box art, title screens, and gameplay screens; it deliberately does not pretend its game logos are cartridge-label scans. The MobyGames fetcher supplies physical media scans for `AssetKind.CARTRIDGE`; see [mobygames-media-provider.md](mobygames-media-provider.md).

## Regenerating or adjusting masks

The deterministic extraction tool is:

```text
tools/extract_cartridge_templates.py
```

Run it with a Python environment containing Pillow. In the Codex desktop workspace it was run with the bundled Python runtime.

Template definitions contain:

- a source sheet and crop rectangle;
- one or more shell silhouette shapes;
- one or more label-area shapes;
- optional subtraction shapes for disc/UMD center holes.

After changing a crop or shape, rerun the tool and inspect `docs/ai/cartridge-templates-preview.png`. To add a new runtime template, also register its ID, caption, and optional platform tags in `CartridgeTemplate.java`.

## Relevant source files

- `romlib/.../ui/carts/CartridgeLayout.java`: mask-aware compositor;
- `rom-manager/.../gui/resources/CartridgeTemplate.java`: template catalog and platform defaults;
- `rom-manager/.../data/romset/Settings.java`: per-dataset selection;
- `rom-manager/.../gui/ViewMenu.java`: fixed-order asset visibility controls;
- `rom-manager/.../gui/gameinfo/InfoPanel.java`: controls, source selection, and rendering;
- `rom-manager/tools/extract_cartridge_templates.py`: deterministic sheet extraction.
