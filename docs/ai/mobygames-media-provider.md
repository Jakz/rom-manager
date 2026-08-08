# MobyGames physical-media provider

## Why this provider

MobyGames cover groups explicitly classify package images by `scan_of`. A value of `Media` means the physical floppy, disc, or cartridge, so it maps cleanly to ROM Manager's `AssetKind.CARTRIDGE` label-art slot.

The implementation uses these API endpoints:

- `/platforms` to resolve current platform IDs by name;
- `/games` to find title candidates on the selected platform;
- `/games/{game_id}/platforms/{platform_id}/covers` to retrieve cover groups;
- only cover entries whose `scan_of` value is `Media` are offered.

Official references:

- <https://www.mobygames.com/info/api/>
- <https://www.mobygames.com/info/standards/>
- <https://www.mobygames.com/api/subscribe/>

## Configuration

Enable **MobyGames Media Fetcher** as a data-fetcher plugin. Its `API Key or File` setting accepts either:

- the API key itself; or
- a path to a text file whose first non-empty line is the key.

For compatibility with the previous unfinished fetcher, leaving the setting empty still checks `mobykey.txt` in the application working directory. The key is URL-encoded for requests and is never included in plugin log messages.

MobyGames API access requires a current API-enabled subscription and attribution under its terms. MobyPlus by itself does not include API access; the current personal-project tier is the separate Hobbyist API subscription. ROM Manager enforces a minimum one-second delay between API calls even if the configured delay is lower.

An HTTP 401 response means MobyGames rejected the supplied credential. The fetcher reports this as an authentication problem rather than incorrectly continuing with a "no matching game" message.

## Manual test

1. Open a dataset whose platform has a matching MobyGames platform, such as Game Boy, Game Boy Advance, NES, or Nintendo 64.
2. In the dataset's **Assets** menu, enable **Cartridge Label** and **Render cartridge/media shell**.
3. Enable **MobyGames Media Fetcher** in the dataset's plugin settings.
4. Select a game, click **Download Assets**, and choose **MobyGames Media Fetcher** if the source menu appears.
5. If MobyGames returns multiple game titles, choose the correct one.
6. Choose a physical-media result. Results are separated by package region and media description when supplied by MobyGames.
7. The selected scan is converted to PNG, saved through the configured cartridge `AssetData`, and rendered inside the selected shell mask.

The current data model stores one cartridge/media label per game. Multi-disc entries are therefore offered as separate choices, and selecting one replaces the currently stored label.

## Verification

`tools/verification/jack/rm/plugins/fetchers/MobyGamesMediaParserCheck.java` is an offline executable check. It verifies that front-cover images are rejected, physical `Media` entries are retained, regions are included in selection captions, and each result maps to exactly one cartridge-label asset. It also checks that DAT suffixes such as `(Europe)` are removed while meaningful title punctuation is preserved for the first API query.

The production fetcher and the offline check compile with `--release 10`. The offline check prints:

```text
MobyGames physical-media parser: OK
```

A live call is intentionally left as an application-level manual test because it transmits the user's private subscription key to MobyGames.

## Alternative considered

ScreenScraper also exposes physical support images (`support-2D`) and is a good future provider. Its API requires application/developer credentials issued after presenting the software to ScreenScraper, in addition to optional end-user credentials. That makes it less suitable for the first immediately testable provider, but the generic source-selection UI can support it as another `DATA_FETCHER` plugin later.
