package com.dadoirie.assortedtweaksnfixes.data.mekanism_compat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PigmentMixer {

    public record RGB(int r, int g, int b) {}

    public static final Map<RGB, String> COLORS = new LinkedHashMap<>();

    static {
        COLORS.put(new RGB(64, 64, 64), "mekanism:black");
        COLORS.put(new RGB(54, 107, 208), "mekanism:blue");
        COLORS.put(new RGB(89, 193, 95), "mekanism:green");
        COLORS.put(new RGB(0, 243, 208), "mekanism:cyan");
        COLORS.put(new RGB(48, 255, 249), "mekanism:aqua");
        COLORS.put(new RGB(201, 7, 31), "mekanism:dark_red");
        COLORS.put(new RGB(164, 96, 217), "mekanism:purple");
        COLORS.put(new RGB(255, 161, 96), "mekanism:orange");
        COLORS.put(new RGB(122, 122, 122), "mekanism:gray");
        COLORS.put(new RGB(207, 207, 207), "mekanism:light_gray");
        COLORS.put(new RGB(85, 158, 255), "mekanism:light_blue");
        COLORS.put(new RGB(117, 255, 137), "mekanism:lime");
        COLORS.put(new RGB(255, 56, 60), "mekanism:red");
        COLORS.put(new RGB(213, 94, 203), "mekanism:pink");
        COLORS.put(new RGB(255, 221, 79), "mekanism:yellow");
        COLORS.put(new RGB(255, 255, 255), "mekanism:white");
        COLORS.put(new RGB(161, 118, 73), "mekanism:brown");
        COLORS.put(new RGB(255, 0, 255), "mekanism:magenta");

        COLORS.put(new RGB(123, 39, 19), "mekanism:dd_maroon");
        COLORS.put(new RGB(255, 94, 100), "mekanism:dd_rose");
        COLORS.put(new RGB(223, 119, 88), "mekanism:dd_coral");
        COLORS.put(new RGB(51, 30, 87), "mekanism:dd_indigo");
        COLORS.put(new RGB(21, 61, 100), "mekanism:dd_navy");
        COLORS.put(new RGB(76, 94, 134), "mekanism:dd_slate");
        COLORS.put(new RGB(140, 143, 42), "mekanism:dd_olive");
        COLORS.put(new RGB(215, 175, 0), "mekanism:dd_amber");
        COLORS.put(new RGB(225, 213, 163), "mekanism:dd_beige");
        COLORS.put(new RGB(47, 123, 103), "mekanism:dd_teal");
        COLORS.put(new RGB(56, 206, 125), "mekanism:dd_mint");
        COLORS.put(new RGB(94, 240, 204), "mekanism:dd_aqua");
        COLORS.put(new RGB(37, 87, 20), "mekanism:dd_verdant");
        COLORS.put(new RGB(50, 163, 38), "mekanism:dd_forest");
        COLORS.put(new RGB(207, 97, 33), "mekanism:dd_ginger");
        COLORS.put(new RGB(244, 156, 93), "mekanism:dd_tan");
    }

    private static final Path RECIPE_OUTPUT = Path.of(
            "src/generated/resources/data/assortedtweaksnfixes/recipe/mekanism_compat/dye_depot_compat/mixing"
    );

    public static void generateRecipes() throws IOException {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        if (!Files.exists(RECIPE_OUTPUT)) Files.createDirectories(RECIPE_OUTPUT);

        for (Entry<RGB, String> left : COLORS.entrySet()) {
            for (Entry<RGB, String> right : COLORS.entrySet()) {
                String leftId = left.getValue();
                String rightId = right.getValue();

                int outputAmount = leftId.equals(rightId) ? 2 : 1;
                String outputId = mixColors(left.getKey(), right.getKey());
                if (outputId == null) continue;

                JsonObject recipe = new JsonObject();
                recipe.addProperty("type", "mekanism:pigment_mixing");

                JsonObject leftInput = new JsonObject();
                leftInput.addProperty("amount", 1);
                leftInput.addProperty("chemical", leftId);

                JsonObject rightInput = new JsonObject();
                rightInput.addProperty("amount", 1);
                rightInput.addProperty("chemical", rightId);

                JsonObject output = new JsonObject();
                output.addProperty("amount", outputAmount);
                output.addProperty("id", outputId);

                recipe.add("left_input", leftInput);
                recipe.add("right_input", rightInput);
                recipe.add("output", output);

                JsonArray conditions = new JsonArray();
                JsonObject cond1 = new JsonObject();
                cond1.addProperty("type", "neoforge:mod_loaded");
                cond1.addProperty("modid", "mekanism");

                JsonObject cond2 = new JsonObject();
                cond2.addProperty("type", "neoforge:mod_loaded");
                cond2.addProperty("modid", "dye_depot");

                conditions.add(cond1);
                conditions.add(cond2);
                recipe.add("neoforge:conditions", conditions);

                String[] names = { leftId.split(":")[1], rightId.split(":")[1] };
                Arrays.sort(names);
                String fileName = names[0] + "_" + names[1] + ".json";

                Path file = RECIPE_OUTPUT.resolve(fileName);
                Files.writeString(file, gson.toJson(recipe));
            }
        }
    }

    private static String mixColors(RGB left, RGB right) {
        double[] hslLeft = rgbToHsl(left);
        double[] hslRight = rgbToHsl(right);

        double hue = averageHue(hslLeft[0], hslRight[0]);

        double lightness = (hslLeft[2] + hslRight[2]) / 2.0;
        double saturation = (hslLeft[1] + hslRight[1]) / 2.0;

        RGB mixRGB = hslToRgb(hue, saturation, lightness);

        return closestColor(mixRGB, left, right);
    }

    private static double[] rgbToHsl(RGB c) {
        double r = c.r / 255.0;
        double g = c.g / 255.0;
        double b = c.b / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));
        double h, s;
        double l = (max + min) / 2.0;

        if (max == min) {
            h = 0;
            s = 0;
        } else {
            double d = max - min;
            s = l > 0.5 ? d / (2.0 - max - min) : d / (max + min);
            if (max == r) h = (g - b) / d + (g < b ? 6 : 0);
            else if (max == g) h = (b - r) / d + 2;
            else h = (r - g) / d + 4;
            h *= 60.0;
        }

        return new double[]{h, s, l};
    }

    private static RGB hslToRgb(double h, double s, double l) {
        double c = (1 - Math.abs(2 * l - 1)) * s;
        double x = c * (1 - Math.abs((h / 60.0) % 2 - 1));
        double m = l - c / 2;

        double r1, g1, b1;

        if (h < 60) { r1 = c; g1 = x; b1 = 0; }
        else if (h < 120) { r1 = x; g1 = c; b1 = 0; }
        else if (h < 180) { r1 = 0; g1 = c; b1 = x; }
        else if (h < 240) { r1 = 0; g1 = x; b1 = c; }
        else if (h < 300) { r1 = x; g1 = 0; b1 = c; }
        else { r1 = c; g1 = 0; b1 = x; }

        int r = (int) Math.round((r1 + m) * 255);
        int g = (int) Math.round((g1 + m) * 255);
        int b = (int) Math.round((b1 + m) * 255);

        return new RGB(r, g, b);
    }

    private static double averageHue(double h1, double h2) {
        double x = Math.cos(Math.toRadians(h1)) + Math.cos(Math.toRadians(h2));
        double y = Math.sin(Math.toRadians(h1)) + Math.sin(Math.toRadians(h2));
        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    private static String closestColor(RGB target, RGB left, RGB right) {
        if (left.equals(right)) return COLORS.get(left);

        String closest = null;
        double minDistance = Double.MAX_VALUE;

        for (Entry<RGB, String> entry : COLORS.entrySet()) {
            RGB c = entry.getKey();

            if (!c.equals(left) && !c.equals(right)) {
                double distance = Math.pow(c.r - target.r, 2)
                        + Math.pow(c.g - target.g, 2)
                        + Math.pow(c.b - target.b, 2);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = entry.getValue();
                }
            }
        }

        return closest;
    }

    public static void main(String[] args) throws IOException {
        generateRecipes();
        System.out.println("Pigment mixing recipes generated in " + RECIPE_OUTPUT);
    }
}