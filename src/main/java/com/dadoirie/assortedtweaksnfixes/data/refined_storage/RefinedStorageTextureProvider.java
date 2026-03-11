package com.dadoirie.assortedtweaksnfixes.data.refined_storage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class RefinedStorageTextureProvider {
    private static final String BASESOURCE_PATH =
            "libs/resources/refinedstorage/assets/refinedstorage/textures";

    private static final String BASESTORE_PATH =
            "src/generated/resources/assets/refinedstorage/textures";

    private static final Map<String, String> SOURCE_TEXTURE = Map.ofEntries(
            Map.entry("block/autocrafter/cutouts/side", "black.png"),
            Map.entry("block/autocrafter/cutouts/top", "black.png"),
            Map.entry("block/autocrafter_manager/cutouts", "black.png"),
            Map.entry("block/autocrafting_monitor/cutouts", "black.png"),
            Map.entry("block/cable", "white.png"),
            Map.entry("block/controller/cutouts", "black.png"),
            Map.entry("block/crafting_grid/cutouts", "black.png"),
            Map.entry("block/detector/cutouts", "black.png"),
            Map.entry("block/disk_interface/cutouts", "black.png"),
            Map.entry("block/grid/cutouts", "black.png"),
            Map.entry("block/network_receiver/cutouts", "black.png"),
            Map.entry("block/network_transmitter/cutouts", "black.png"),
            Map.entry("block/pattern_grid/cutouts", "black.png"),
            Map.entry("block/relay/cutouts/in", "black.png"),
            Map.entry("block/relay/cutouts/out", "black.png"),
            Map.entry("block/security_manager/cutouts/back", "black.png"),
            Map.entry("block/security_manager/cutouts/front", "black.png"),
            Map.entry("block/security_manager/cutouts/left", "black.png"),
            Map.entry("block/security_manager/cutouts/right", "black.png"),
            Map.entry("block/security_manager/cutouts/top", "black.png"),
            Map.entry("block/wireless_transmitter/cutouts", "black.png")
    );

    private record RGB(int r, int g, int b) {
    }

    private static final Map<String, RGB> DYE_COLORS = Map.ofEntries(
            Map.entry("maroon", new RGB(123, 39, 19)),
            Map.entry("rose", new RGB(255, 94, 100)),
            Map.entry("coral", new RGB(223, 119, 88)),
            Map.entry("indigo", new RGB(102, 60, 174)),
            Map.entry("navy", new RGB(42, 122, 200)),
            Map.entry("slate", new RGB(76, 94, 134)),
            Map.entry("olive", new RGB(140, 143, 42)),
            Map.entry("amber", new RGB(215, 175, 0)),
            Map.entry("beige", new RGB(225, 213, 163)),
            Map.entry("teal", new RGB(47, 123, 103)),
            Map.entry("mint", new RGB(56, 206, 125)),
            Map.entry("aqua", new RGB(94, 240, 204)),
            Map.entry("verdant", new RGB(20, 87, 41)),
            Map.entry("forest", new RGB(69, 158, 34)),
            Map.entry("ginger", new RGB(207, 97, 33)),
            Map.entry("tan", new RGB(244, 156, 93))
    );

    public static void run() throws IOException {
        for (Map.Entry<String, String> entry : SOURCE_TEXTURE.entrySet()) {
            String relativePath = entry.getKey();
            String filename = entry.getValue();

            File sourceFile = new File(BASESOURCE_PATH, relativePath + "/" + filename);
            if (!sourceFile.exists()) {
                System.err.println("Source texture missing: " + sourceFile.getAbsolutePath());
                continue;
            }

            BufferedImage source = ImageIO.read(sourceFile);

            for (Map.Entry<String, RGB> dyeEntry : DYE_COLORS.entrySet()) {
                String colorName = dyeEntry.getKey();
                RGB color = dyeEntry.getValue();

                boolean whiteMask = filename.equals("white.png");
                BufferedImage result = recolorMask(source, color, whiteMask);

                File outFile = new File(BASESTORE_PATH, relativePath + "/" + colorName + ".png");
                File parentDir = outFile.getParentFile();
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    throw new IOException("Failed to create directories: " + parentDir);
                }
                ImageIO.write(result, "png", outFile);

                File mcmetaFile = new File(sourceFile.getParent(), filename + ".mcmeta");
                if (mcmetaFile.exists()) {
                    File outMcmeta = new File(outFile.getParent(), colorName + ".png.mcmeta");
                    java.nio.file.Files.copy(mcmetaFile.toPath(), outMcmeta.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("Applied dye color: " + colorName + " -> " + outFile.getAbsolutePath());
            }
        }
    }

    private static BufferedImage recolorMask(BufferedImage mask, RGB dyeColor, boolean whiteMask) {
        int width = mask.getWidth();
        int height = mask.getHeight();

        int dr = dyeColor.r;
        int dg = dyeColor.g;
        int db = dyeColor.b;

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int maskPixel = mask.getRGB(x, y);
                int a = (maskPixel >> 24) & 0xFF;
                int rMask = (maskPixel >> 16) & 0xFF;

                int skip = whiteMask ? 0 : 255;

                if (a == 0 || rMask == skip) {
                    result.setRGB(x, y, maskPixel);
                    continue;
                }

                float lum = whiteMask ? (rMask / 255f) : (1f - (rMask / 255f));
                lum = (float) Math.pow(lum, 2.0);

                int nr = Math.min(255, Math.round(dr * lum));
                int ng = Math.min(255, Math.round(dg * lum));
                int nb = Math.min(255, Math.round(db * lum));

                result.setRGB(x, y, (a << 24) | (nr << 16) | (ng << 8) | nb);
            }
        }

        return result;
    }
}