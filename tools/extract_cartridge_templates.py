"""Build cartridge shell and label-mask resources from the generated source sheets.

The source sheets remain in the repository for provenance. Each template is
cropped onto a consistent transparent canvas and receives a separate mask that
defines where downloaded label artwork may be composited.
"""

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter


ROOT = Path(__file__).resolve().parents[1]
RESOURCE_ROOT = ROOT / "src" / "jack" / "rm" / "gui" / "resources" / "cartridges"
SOURCE_ROOT = ROOT / "docs" / "ai" / "cartridge-sources"
SHELL_ROOT = RESOURCE_ROOT / "shells"
MASK_ROOT = RESOURCE_ROOT / "masks"
PREVIEW_PATH = ROOT / "docs" / "ai" / "cartridge-templates-preview.png"
CANVAS = (640, 440)
PADDING = 18


def rect(box, radius=0):
    return ("roundrect", box, radius)


def polygon(points):
    return ("polygon", points)


def ellipse(box):
    return ("ellipse", box)


TEMPLATES = [
    # cartridge-sheet-01: compact cards and a wide 64-bit style shell
    dict(id="compact-white-card", source="cartridge-sheet-01.png", crop=(85, 365, 375, 655),
         shell=[polygon([(101, 391), (360, 391), (360, 437), (343, 437), (343, 615), (320, 640), (125, 640), (102, 615)])],
         label=[rect((126, 411, 321, 577), 13)]),
    dict(id="nintendo-64", source="cartridge-sheet-01.png", crop=(450, 300, 1045, 665),
         shell=[polygon([(475, 377), (510, 350), (585, 330), (915, 330), (1008, 350), (1032, 379), (1032, 614), (1015, 642), (477, 642)])],
         label=[rect((611, 361, 890, 596), 18)]),
    dict(id="compact-black-card", source="cartridge-sheet-01.png", crop=(1135, 360, 1435, 660),
         shell=[polygon([(1155, 389), (1414, 389), (1414, 438), (1398, 438), (1398, 619), (1377, 642), (1175, 642), (1155, 618)])],
         label=[rect((1181, 405, 1385, 576), 12)]),

    # cartridge-sheet-02: classic handheld cartridge family
    dict(id="game-boy", source="cartridge-sheet-02.png", crop=(105, 275, 490, 680),
         shell=[polygon([(127, 300), (437, 300), (437, 315), (471, 315), (471, 662), (127, 662)])],
         label=[rect((169, 401, 429, 607), 9)]),
    dict(id="game-boy-color", source="cartridge-sheet-02.png", crop=(565, 275, 975, 680),
         shell=[polygon([(583, 296), (909, 296), (909, 307), (949, 307), (949, 661), (584, 661)])],
         label=[rect((628, 398, 909, 602), 9)]),
    dict(id="game-boy-advance", source="cartridge-sheet-02.png", crop=(1050, 470, 1465, 690),
         shell=[polygon([(1071, 494), (1438, 494), (1438, 530), (1415, 530), (1415, 660), (1092, 660), (1092, 530), (1071, 530)])],
         label=[rect((1128, 538, 1384, 633), 8)]),

    # cartridge-sheet-03: home-console and DS family
    dict(id="nes", source="cartridge-sheet-03.png", crop=(55, 275, 465, 715),
         shell=[polygon([(72, 294), (455, 294), (455, 624), (430, 696), (98, 696), (73, 624)])],
         label=[rect((239, 296, 403, 563), 8)]),
    dict(id="super-nintendo", source="cartridge-sheet-03.png", crop=(495, 320, 1050, 715),
         shell=[polygon([(512, 352), (549, 344), (599, 344), (599, 348), (942, 348), (942, 344), (1001, 348), (1034, 367), (1034, 677), (1011, 699), (530, 699), (512, 679)])],
         label=[rect((568, 376, 978, 502), 13)]),
    dict(id="nintendo-ds", source="cartridge-sheet-03.png", crop=(1165, 540, 1325, 715),
         shell=[rect((1184, 554, 1307, 699), 16)],
         label=[rect((1198, 569, 1294, 605), 3)]),

    # cartridge-sheet-04: black cartridge variants
    dict(id="atari-2600", source="cartridge-sheet-04.png", crop=(85, 85, 425, 450),
         shell=[rect((102, 107, 407, 433), 13)],
         label=[rect((130, 130, 381, 250), 9)]),
    dict(id="mega-drive", source="cartridge-sheet-04.png", crop=(535, 90, 965, 450),
         shell=[rect((555, 110, 942, 431), 16)],
         label=[rect((591, 132, 906, 266), 7)]),
    dict(id="game-gear", source="cartridge-sheet-04.png", crop=(45, 565, 475, 885),
         shell=[polygon([(63, 590), (446, 590), (452, 615), (438, 640), (438, 855), (70, 855), (70, 640), (54, 615)])],
         label=[rect((99, 660, 420, 788), 7)]),
    dict(id="master-system", source="cartridge-sheet-04.png", crop=(585, 610, 950, 890),
         shell=[rect((606, 633, 925, 864), 13)],
         label=[rect((651, 678, 903, 788), 7)]),

    # cartridge-sheet-05: additional handheld cartridge variants
    dict(id="neo-geo-pocket", source="cartridge-sheet-05.png", crop=(50, 90, 445, 475),
         shell=[polygon([(76, 127), (405, 111), (421, 129), (421, 452), (77, 452)])],
         label=[rect((113, 177, 385, 315), 12)]),
    dict(id="wonderswan", source="cartridge-sheet-05.png", crop=(500, 90, 980, 470),
         shell=[polygon([(518, 126), (577, 120), (906, 120), (963, 134), (963, 424), (907, 446), (578, 446), (519, 423)])],
         label=[rect((586, 143, 897, 266), 7)]),
    dict(id="atari-lynx", source="cartridge-sheet-05.png", crop=(35, 570, 480, 885),
         shell=[rect((52, 586, 462, 858), 12)],
         label=[rect((87, 598, 428, 666), 5)]),
    dict(id="nintendo-3ds", source="cartridge-sheet-05.png", crop=(555, 595, 950, 890),
         shell=[rect((575, 613, 931, 868), 14)],
         label=[rect((615, 662, 899, 792), 7)]),

    # media-sheet-01: optical media variants
    dict(id="mini-optical-disc", source="media-sheet-01.png", crop=(105, 145, 430, 475),
         shell=[ellipse((129, 174, 407, 452))],
         label=[ellipse((139, 184, 397, 442))], label_subtract=[ellipse((213, 248, 324, 359))]),
    dict(id="optical-disc-silver", source="media-sheet-01.png", crop=(485, 15, 980, 510),
         shell=[ellipse((518, 37, 953, 487))],
         label=[ellipse((528, 47, 943, 477))], label_subtract=[ellipse((677, 205, 795, 323))]),
    dict(id="optical-disc-blue-ring", source="media-sheet-01.png", crop=(40, 505, 500, 965),
         shell=[ellipse((73, 535, 477, 940))],
         label=[ellipse((83, 545, 467, 930))], label_subtract=[ellipse((211, 655, 329, 773))]),
    dict(id="optical-disc-reflective", source="media-sheet-01.png", crop=(525, 510, 975, 960),
         shell=[ellipse((551, 537, 947, 927))],
         label=[ellipse((561, 547, 937, 917))], label_subtract=[ellipse((681, 657, 807, 783))]),

    # media-sheet-02: additional optical media and UMD
    dict(id="optical-disc-classic", source="media-sheet-02.png", crop=(135, 20, 535, 460),
         shell=[ellipse((166, 66, 520, 434))],
         label=[ellipse((176, 76, 510, 424))], label_subtract=[ellipse((282, 188, 395, 301))]),
    dict(id="optical-disc-modern", source="media-sheet-02.png", crop=(545, 20, 990, 465),
         shell=[ellipse((575, 64, 965, 440))],
         label=[ellipse((585, 74, 955, 430))], label_subtract=[ellipse((709, 181, 826, 298))]),
    dict(id="optical-disc-blue", source="media-sheet-02.png", crop=(1000, 20, 1465, 465),
         shell=[ellipse((1032, 65, 1438, 439))],
         label=[ellipse((1042, 75, 1428, 429))], label_subtract=[ellipse((1170, 181, 1294, 305))]),
    dict(id="psp-umd", source="media-sheet-02.png", crop=(285, 545, 690, 970),
         shell=[polygon([(341, 605), (379, 582), (572, 582), (636, 612), (650, 823), (620, 890), (353, 890), (326, 824)])],
         label=[ellipse((354, 615, 625, 867))], label_subtract=[ellipse((456, 695, 530, 769))]),
    dict(id="optical-disc-green", source="media-sheet-02.png", crop=(815, 505, 1265, 970),
         shell=[ellipse((846, 541, 1238, 933))],
         label=[ellipse((856, 551, 1228, 923))], label_subtract=[ellipse((979, 674, 1100, 795))]),
]


def draw_shapes(size, shapes):
    mask = Image.new("L", size, 0)
    draw = ImageDraw.Draw(mask)
    for shape in shapes:
        kind = shape[0]
        if kind == "roundrect":
            draw.rounded_rectangle(shape[1], radius=shape[2], fill=255)
        elif kind == "polygon":
            draw.polygon(shape[1], fill=255)
        elif kind == "ellipse":
            draw.ellipse(shape[1], fill=255)
        else:
            raise ValueError(f"unknown shape {kind}")
    return mask


def normalize(image, crop, resample):
    image = image.crop(crop)
    max_size = (CANVAS[0] - PADDING * 2, CANVAS[1] - PADDING * 2)
    image.thumbnail(max_size, resample)
    canvas = Image.new(image.mode, CANVAS, 0 if image.mode == "L" else (0, 0, 0, 0))
    x = (CANVAS[0] - image.width) // 2
    y = (CANVAS[1] - image.height) // 2
    canvas.paste(image, (x, y))
    return canvas


def build_template(template):
    source = Image.open(SOURCE_ROOT / template["source"]).convert("RGBA")
    shell_mask = draw_shapes(source.size, template["shell"]).filter(ImageFilter.GaussianBlur(0.8))
    shell = source.copy()
    shell.putalpha(shell_mask)

    label_mask = draw_shapes(source.size, template["label"])
    for shape in template.get("label_subtract", []):
        subtraction = draw_shapes(source.size, [shape])
        label_mask.paste(0, mask=subtraction)
    label_mask = label_mask.filter(ImageFilter.GaussianBlur(0.45))

    shell = normalize(shell, template["crop"], Image.Resampling.LANCZOS)
    label_mask = normalize(label_mask, template["crop"], Image.Resampling.LANCZOS)

    SHELL_ROOT.mkdir(parents=True, exist_ok=True)
    MASK_ROOT.mkdir(parents=True, exist_ok=True)
    shell.save(SHELL_ROOT / f"{template['id']}.png", optimize=True)
    label_mask.save(MASK_ROOT / f"{template['id']}.png", optimize=True)


def checkerboard(size, tile=16):
    image = Image.new("RGBA", size, (238, 238, 238, 255))
    draw = ImageDraw.Draw(image)
    for y in range(0, size[1], tile):
        for x in range(0, size[0], tile):
            if (x // tile + y // tile) % 2:
                draw.rectangle((x, y, x + tile - 1, y + tile - 1), fill=(210, 210, 210, 255))
    return image


def build_preview():
    cell = (320, 250)
    columns = 4
    rows = (len(TEMPLATES) + columns - 1) // columns
    preview = Image.new("RGB", (cell[0] * columns, cell[1] * rows), (245, 245, 245))

    label_art = Image.new("RGBA", CANVAS, (0, 0, 0, 0))
    art_draw = ImageDraw.Draw(label_art)
    colors = ((219, 70, 70, 255), (236, 181, 62, 255), (48, 139, 196, 255), (68, 176, 123, 255))
    stripe = CANVAS[0] // len(colors)
    for i, color in enumerate(colors):
        art_draw.rectangle((i * stripe, 0, (i + 1) * stripe, CANVAS[1]), fill=color)

    for index, template in enumerate(TEMPLATES):
        shell = Image.open(SHELL_ROOT / f"{template['id']}.png").convert("RGBA")
        mask = Image.open(MASK_ROOT / f"{template['id']}.png").convert("L")
        mapped_art = label_art.copy()
        mapped_art.putalpha(mask)
        rendered = Image.alpha_composite(shell, mapped_art)
        rendered.thumbnail((300, 206), Image.Resampling.LANCZOS)

        cell_image = checkerboard((cell[0], cell[1] - 28))
        cell_image.alpha_composite(rendered, ((cell_image.width - rendered.width) // 2,
                                              (cell_image.height - rendered.height) // 2))
        x = index % columns * cell[0]
        y = index // columns * cell[1]
        preview.paste(cell_image.convert("RGB"), (x, y))
        ImageDraw.Draw(preview).text((x + 8, y + cell[1] - 23), template["id"], fill=(25, 25, 25))

    PREVIEW_PATH.parent.mkdir(parents=True, exist_ok=True)
    preview.save(PREVIEW_PATH, optimize=True)


def main():
    for template in TEMPLATES:
        build_template(template)
    build_preview()
    print(f"Built {len(TEMPLATES)} cartridge templates in {RESOURCE_ROOT}")


if __name__ == "__main__":
    main()
