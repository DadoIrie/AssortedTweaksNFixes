package com.dadoirie.assortedtweaksnfixes.data.condiments;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateTextureProvider {
    private static final String BASESOURCE_PATH =
            "libs/resources/condiments/assets/condiments/textures/block";

    private static final String BASESTORE_PATH =
            "src/generated/resources/assets/condiments/textures/block";

    private static final String SOURCE_TEXTURE = "gray_crate.png";

    private static final String[] COLORS = {
            "maroon","rose","coral","indigo","navy","slate","olive","amber",
            "beige","teal","mint","aqua","verdant","forest","ginger","tan"
    };

    // Border brightness boost per color (1.0 = no change, >1.0 = brighter)
    private static final Map<String, Double> BORDER_BRIGHTNESS = new HashMap<>();
    static {
        BORDER_BRIGHTNESS.put("indigo", 2.5);
        BORDER_BRIGHTNESS.put("navy",   2.5);
    }

    public static void run() throws IOException {
        BufferedImage source = ImageIO.read(new File(BASESOURCE_PATH, SOURCE_TEXTURE));
        BufferedImage borderTemplate = ImageIO.read(new File(BASESOURCE_PATH, "crate.png"));

        for (String colorName : COLORS) {
            BufferedImage wool = ImageIO.read(new File(
                    "libs/resources/dye_depot/assets/dye_depot/textures/block",
                    colorName + "_wool.png"
            ));
            double borderBoost = BORDER_BRIGHTNESS.getOrDefault(colorName, 1.0);
            BufferedImage result = recolorCrate(source, borderTemplate, wool, borderBoost);

            File outFile = new File(BASESTORE_PATH, colorName + "_crate.png");
            File parentDir = outFile.getParentFile();
            if (!parentDir.exists() && !parentDir.mkdirs()) {
                throw new IOException("Failed to create directories: " + parentDir);
            }
            ImageIO.write(result, "png", outFile);
            System.out.println("Applied dye color: " + colorName + " -> " + outFile.getAbsolutePath());
        }
    }

    private static BufferedImage recolorCrate(BufferedImage mask, BufferedImage borderTemplate, BufferedImage wool, double borderBoost) {
        int width = mask.getWidth();
        int height = mask.getHeight();
        int woolWidth = wool.getWidth();
        int woolHeight = wool.getHeight();

        // Step 1: collect all unique gray values from the inner (non-border) mask pixels
        List<Integer> uniqueGrays = new ArrayList<>();
        for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
                int p = mask.getRGB(x, y);
                int r = (p >> 16) & 0xFF;
                int g = (p >> 8) & 0xFF;
                int b = p & 0xFF;
                int gray = (r + g + b) / 3;
                if (!uniqueGrays.contains(gray)) {
                    uniqueGrays.add(gray);
                }
            }
        }
        Collections.sort(uniqueGrays);

        // Step 2: collect all unique colors from the wool texture, sorted by brightness
        List<Integer> uniqueWoolColors = new ArrayList<>();
        for (int wy = 0; wy < woolHeight; wy++) {
            for (int wx = 0; wx < woolWidth; wx++) {
                int p = wool.getRGB(wx, wy);
                if (!uniqueWoolColors.contains(p)) {
                    uniqueWoolColors.add(p);
                }
            }
        }
        uniqueWoolColors.sort((a, b2) -> {
            int brA = ((a >> 16) & 0xFF) + ((a >> 8) & 0xFF) + (a & 0xFF);
            int brB = ((b2 >> 16) & 0xFF) + ((b2 >> 8) & 0xFF) + (b2 & 0xFF);
            return Integer.compare(brA, brB);
        });

        // Step 3: map each unique gray to a wool color by proportional index
        Map<Integer, Integer> grayToWool = new HashMap<>();
        int numGrays = uniqueGrays.size();
        int numWool = uniqueWoolColors.size();
        for (int i = 0; i < numGrays; i++) {
            int woolIndex = (int) Math.round((double) i / (numGrays - 1) * (numWool - 1));
            grayToWool.put(uniqueGrays.get(i), uniqueWoolColors.get(woolIndex));
        }

        // Step 4: render
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int maskPixel = mask.getRGB(x, y);
                int mr = (maskPixel >> 16) & 0xFF;
                int mg = (maskPixel >> 8) & 0xFF;
                int mb = maskPixel & 0xFF;
                int gray = (mr + mg + mb) / 3;

                boolean isBorder = x < 2 || x >= width - 2 || y < 2 || y >= height - 2;

                if (isBorder) {
                    // Border: multiply blend crate border template with wool color + optional brightness boost
                    int woolPixel = wool.getRGB(x % woolWidth, y % woolHeight);
                    int wr = Math.min(255, (int) (((woolPixel >> 16) & 0xFF) * borderBoost));
                    int wg = Math.min(255, (int) (((woolPixel >> 8)  & 0xFF) * borderBoost));
                    int wb = Math.min(255, (int) ((woolPixel        & 0xFF)  * borderBoost));

                    int borderPixel = borderTemplate.getRGB(x, y);
                    int ba = (borderPixel >> 24) & 0xFF;
                    int br = (borderPixel >> 16) & 0xFF;
                    int bg = (borderPixel >> 8)  & 0xFF;
                    int bb =  borderPixel        & 0xFF;

                    result.setRGB(x, y, (ba << 24) | ((br * wr / 255) << 16) | ((bg * wg / 255) << 8) | (bb * wb / 255));
                } else {
                    // Inner: exact wool color mapped from this gray value
                    int woolColor = grayToWool.get(gray);
                    int wr = (woolColor >> 16) & 0xFF;
                    int wg = (woolColor >> 8)  & 0xFF;
                    int wb =  woolColor        & 0xFF;
                    result.setRGB(x, y, (0xFF << 24) | (wr << 16) | (wg << 8) | wb);
                }
            }
        }

        return result;
    }
}