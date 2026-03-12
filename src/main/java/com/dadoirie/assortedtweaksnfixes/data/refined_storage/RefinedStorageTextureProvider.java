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
            Map.entry("block/autocrafter/cutouts/side",          "black.png"),
            Map.entry("block/autocrafter/cutouts/top",           "black.png"),
            Map.entry("block/autocrafter_manager/cutouts",       "black.png"),
            Map.entry("block/autocrafting_monitor/cutouts",      "black.png"),
            Map.entry("block/cable",                             "black.png"),
            Map.entry("block/controller/cutouts",                "black.png"),
            Map.entry("block/crafting_grid/cutouts",             "black.png"),
            Map.entry("block/detector/cutouts",                  "black.png"),
            Map.entry("block/disk_interface/cutouts",            "black.png"),
            Map.entry("block/grid/cutouts",                      "black.png"),
            Map.entry("block/network_receiver/cutouts",          "black.png"),
            Map.entry("block/network_transmitter/cutouts",       "black.png"),
            Map.entry("block/pattern_grid/cutouts",              "black.png"),
            Map.entry("block/relay/cutouts/in",                  "black.png"),
            Map.entry("block/relay/cutouts/out",                 "black.png"),
            Map.entry("block/security_manager/cutouts/back",     "black.png"),
            Map.entry("block/security_manager/cutouts/front",    "black.png"),
            Map.entry("block/security_manager/cutouts/left",     "black.png"),
            Map.entry("block/security_manager/cutouts/right",    "black.png"),
            Map.entry("block/security_manager/cutouts/top",      "black.png"),
            Map.entry("block/wireless_transmitter/cutouts",      "black.png")
    );

    private record RGB(int r, int g, int b) {}

    // Three-stop color ramp: dark (recesses) -> mid (identity) -> highlight (glow core)
    private record ColorDef(RGB dark, RGB mid, RGB highlight) {}

    private static final Map<String, ColorDef> DYE_COLORS = Map.ofEntries(
            Map.entry("maroon",  new ColorDef(
                    new RGB(120,  30,  10),   // br=160
                    new RGB(200,  65,  30),   // br=295
                    new RGB(255, 145,  90))), // br=490
            Map.entry("rose",    new ColorDef(
                    new RGB(180,  45,  55),   // br=280
                    new RGB(240, 100, 110),   // br=450
                    new RGB(255, 210, 210))), // br=675
            Map.entry("coral",   new ColorDef(
                    new RGB(175,  75,  35),   // br=285
                    new RGB(235, 125,  75),   // br=435
                    new RGB(255, 210, 160))), // br=625
            Map.entry("ginger",  new ColorDef(
                    new RGB(165,  65,  12),   // br=242
                    new RGB(225, 110,  35),   // br=370
                    new RGB(255, 195,  95))), // br=545
            Map.entry("amber",   new ColorDef(
                    new RGB(160, 120,   0),   // br=280
                    new RGB(225, 175,   0),   // br=400
                    new RGB(255, 240, 130))), // br=625
            Map.entry("tan",     new ColorDef(
                    new RGB(160,  95,  28),   // br=283
                    new RGB(225, 155,  85),   // br=465
                    new RGB(255, 225, 180))), // br=660
            Map.entry("indigo",  new ColorDef(
                    new RGB( 65,  28, 145),   // br=238
                    new RGB(140,  85, 235),   // br=460
                    new RGB(215, 185, 255))), // br=655
            Map.entry("navy",    new ColorDef(
                    new RGB( 15,  65, 145),   // br=225
                    new RGB( 45, 130, 220),   // br=395
                    new RGB(125, 215, 255))), // br=595
            Map.entry("slate",   new ColorDef(
                    new RGB( 40,  55, 105),   // br=200
                    new RGB( 90, 110, 165),   // br=365
                    new RGB(170, 190, 240))), // br=600
            Map.entry("aqua",    new ColorDef(
                    new RGB( 22, 160, 130),   // br=312
                    new RGB( 65, 220, 185),   // br=470
                    new RGB(185, 255, 245))), // br=685
            Map.entry("teal",    new ColorDef(
                    new RGB( 15,  80,  65),   // br=160
                    new RGB( 48, 145, 120),   // br=313
                    new RGB(100, 235, 195))), // br=530
            Map.entry("mint",    new ColorDef(
                    new RGB( 18, 145,  80),   // br=243
                    new RGB( 58, 220, 135),   // br=413
                    new RGB(158, 255, 200))), // br=613
            Map.entry("olive",   new ColorDef(
                    new RGB( 80,  88,  14),   // br=182
                    new RGB(155, 162,  45),   // br=362
                    new RGB(220, 232, 105))), // br=557
            Map.entry("verdant", new ColorDef(
                    new RGB( 14,  65,  25),   // br=104
                    new RGB( 42, 155,  72),   // br=269
                    new RGB(105, 240, 125))), // br=470
            Map.entry("forest",  new ColorDef(
                    new RGB( 28,  92,  10),   // br=130
                    new RGB( 72, 175,  35),   // br=282
                    new RGB(148, 248,  92))), // br=488
            Map.entry("beige",   new ColorDef(
                    new RGB(155, 138,  75),   // br=368
                    new RGB(220, 205, 150),   // br=575
                    new RGB(255, 252, 225)))  // br=732
    );

    public static void run() throws IOException {
        for (Map.Entry<String, String> entry : SOURCE_TEXTURE.entrySet()) {
            String relativePath = entry.getKey();
            String filename     = entry.getValue();

            File sourceFile = new File(BASESOURCE_PATH, relativePath + "/" + filename);
            if (!sourceFile.exists()) {
                System.err.println("Source texture missing: " + sourceFile.getAbsolutePath());
                continue;
            }

            BufferedImage source     = ImageIO.read(sourceFile);
            boolean       isCable    = relativePath.contains("cable");
            File          mcmetaFile = new File(sourceFile.getParent(), filename + ".mcmeta");
            boolean       isAnimated = mcmetaFile.exists();

            int[] range = scanMaskRange(source);
            int maskMin = range[0];
            int maskMax = range[1];

            for (Map.Entry<String, ColorDef> dyeEntry : DYE_COLORS.entrySet()) {
                String   colorName = dyeEntry.getKey();
                ColorDef colorDef  = dyeEntry.getValue();

                BufferedImage result;
                if (isAnimated) {
                    result = recolorAnimated(source, colorDef);
                } else {
                    float pulse = isCable ? 0.85f : 1.0f;
                    result = recolorStatic(source, colorDef, maskMin, maskMax, pulse);
                }

                File outFile   = new File(BASESTORE_PATH, relativePath + "/" + colorName + ".png");
                File parentDir = outFile.getParentFile();
                if (!parentDir.exists() && !parentDir.mkdirs()) {
                    throw new IOException("Failed to create directories: " + parentDir);
                }
                ImageIO.write(result, "png", outFile);

                if (isAnimated) {
                    File outMcmeta = new File(outFile.getParent(), colorName + ".png.mcmeta");
                    java.nio.file.Files.copy(mcmetaFile.toPath(), outMcmeta.toPath(),
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("Applied: " + colorName + " -> " + outFile.getAbsolutePath());
            }
        }
    }

    // Returns [min, max] RGB brightness across all visible non-marker pixels.
    private static int[] scanMaskRange(BufferedImage mask) {
        int min = 765, max = 0;
        for (int y = 0; y < mask.getHeight(); y++) {
            for (int x = 0; x < mask.getWidth(); x++) {
                int pixel = mask.getRGB(x, y);
                if (((pixel >> 24) & 0xFF) == 0) continue;
                int br = brightness(pixel);
                if (br >= 750) continue;
                if (br < min) min = br;
                if (br > max) max = br;
            }
        }
        if (min >= max) { min = 0; max = 765; }
        return new int[]{ min, max };
    }

    private static float normalize(int pixel, int maskMin, int maskMax) {
        return (float)(brightness(pixel) - maskMin) / (maskMax - maskMin);
    }

    private static int brightness(int pixel) {
        return ((pixel >> 16) & 0xFF) + ((pixel >> 8) & 0xFF) + (pixel & 0xFF);
    }

    // Recolor a static mask. pulse=1.0 for active blocks, 0.85 for cables.
    private static BufferedImage recolorStatic(BufferedImage mask, ColorDef def,
                                               int maskMin, int maskMax, float pulse) {
        int width  = mask.getWidth();
        int height = mask.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = mask.getRGB(x, y);
                int a     = (pixel >> 24) & 0xFF;
                if (a == 0 || brightness(pixel) >= 750) { result.setRGB(x, y, pixel); continue; }
                float lum = normalize(pixel, maskMin, maskMax);
                int[] c   = applyGlow(def, lum, pulse);
                result.setRGB(x, y, (a << 24) | (c[0] << 16) | (c[1] << 8) | c[2]);
            }
        }
        return result;
    }

    // Recolor the animated controller mask (12-frame vertical strip).
    // The RS mask encodes its animation directly in per-frame RGB values.
    // Each pixel's brightness curve is decomposed into pulse + flicker:
    //   smooth  = 3-frame box filter        -> slow pulse shape
    //   flicker = raw - smooth              -> fast per-frame noise
    //   output  = smooth + flicker * 0.15   -> subtle organic feel, pulse dominant
    // The smoothed range is mapped into lum 0.0-0.30 (dark zone) so the output
    // swing stays proportional to RS regardless of ramp width.
    private static BufferedImage recolorAnimated(BufferedImage mask, ColorDef def) {
        int frameSize   = mask.getWidth();
        int totalHeight = mask.getHeight();
        int frameCount  = totalHeight / frameSize;
        BufferedImage result = new BufferedImage(frameSize, totalHeight, BufferedImage.TYPE_INT_ARGB);

        final float FLICKER_AMOUNT = 0.15f;

        // Raw brightness per pixel per frame (-1 = transparent/skip)
        int[][] rawBr = new int[frameSize * frameSize][frameCount];
        for (int y = 0; y < frameSize; y++) {
            for (int x = 0; x < frameSize; x++) {
                int idx = y * frameSize + x;
                for (int f = 0; f < frameCount; f++) {
                    int pixel = mask.getRGB(x, y + f * frameSize);
                    int a     = (pixel >> 24) & 0xFF;
                    if (a == 0) { rawBr[idx][f] = -1; continue; }
                    int br = brightness(pixel);
                    rawBr[idx][f] = (br >= 750) ? -1 : br;
                }
            }
        }

        // Smooth each pixel's curve with a 3-frame box filter
        float[][] smoothBr = new float[frameSize * frameSize][frameCount];
        for (int i = 0; i < frameSize * frameSize; i++) {
            for (int f = 0; f < frameCount; f++) {
                if (rawBr[i][f] < 0) { smoothBr[i][f] = -1; continue; }
                float sum = 0; int count = 0;
                for (int k = Math.max(0, f - 1); k <= Math.min(frameCount - 1, f + 1); k++) {
                    if (rawBr[i][k] >= 0) { sum += rawBr[i][k]; count++; }
                }
                smoothBr[i][f] = (count > 0) ? sum / count : rawBr[i][f];
            }
        }

        // Per-pixel min/max of the smoothed curve
        float[] pxSmoothMin = new float[frameSize * frameSize];
        float[] pxSmoothMax = new float[frameSize * frameSize];
        for (int i = 0; i < frameSize * frameSize; i++) {
            float mn = 99999, mx = 0;
            for (int f = 0; f < frameCount; f++) {
                if (smoothBr[i][f] >= 0) { mn = Math.min(mn, smoothBr[i][f]); mx = Math.max(mx, smoothBr[i][f]); }
            }
            pxSmoothMin[i] = (mn == 99999) ? 0 : mn;
            pxSmoothMax[i] = (mx <= pxSmoothMin[i]) ? pxSmoothMin[i] + 1 : mx;
        }

        for (int frame = 0; frame < frameCount; frame++) {
            int yOff = frame * frameSize;
            for (int y = 0; y < frameSize; y++) {
                for (int x = 0; x < frameSize; x++) {
                    int pixel = mask.getRGB(x, y + yOff);
                    int a     = (pixel >> 24) & 0xFF;
                    int idx   = y * frameSize + x;

                    if (a == 0 || rawBr[idx][frame] < 0) {
                        result.setRGB(x, y + yOff, pixel);
                        continue;
                    }

                    float smooth   = smoothBr[idx][frame];
                    float flicker  = rawBr[idx][frame] - smooth;
                    float range    = pxSmoothMax[idx] - pxSmoothMin[idx];
                    float t        = (smooth - pxSmoothMin[idx]) / range;
                    float flickerT = (flicker / range) * FLICKER_AMOUNT;
                    float lum      = Math.max(0f, Math.min(0.30f, (t + flickerT) * 0.30f));

                    int[] c = applyGlow(def, lum, 1.0f);
                    result.setRGB(x, y + yOff, (a << 24) | (c[0] << 16) | (c[1] << 8) | c[2]);
                }
            }
        }
        return result;
    }

    // Three-zone color ramp:
    //   0.00-0.35 dark : lerp dark -> mid
    //   0.35-0.65 mid  : solid mid color (RS's punchy identity surface)
    //   0.65-1.00 hot  : lerp mid -> highlight, scaled by pulse
    private static int[] applyGlow(ColorDef def, float lum, float pulse) {
        int dr = def.dark().r(),      dg = def.dark().g(),      db = def.dark().b();
        int mr = def.mid().r(),       mg = def.mid().g(),       mb = def.mid().b();
        int hr = def.highlight().r(), hg = def.highlight().g(), hb = def.highlight().b();
        int nr, ng, nb;
        if (lum < 0.35f) {
            float t = lum / 0.35f;
            nr = Math.round(dr + (mr - dr) * t);
            ng = Math.round(dg + (mg - dg) * t);
            nb = Math.round(db + (mb - db) * t);
        } else if (lum < 0.65f) {
            nr = mr; ng = mg; nb = mb;
        } else {
            float t = ((lum - 0.65f) / 0.35f) * pulse;
            nr = Math.min(255, Math.round(mr + (hr - mr) * t));
            ng = Math.min(255, Math.round(mg + (hg - mg) * t));
            nb = Math.min(255, Math.round(mb + (hb - mb) * t));
        }
        return new int[]{ nr, ng, nb };
    }
}