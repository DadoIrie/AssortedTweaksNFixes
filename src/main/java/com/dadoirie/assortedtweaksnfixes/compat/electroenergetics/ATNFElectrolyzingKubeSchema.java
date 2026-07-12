package com.dadoirie.assortedtweaksnfixes.compat.electroenergetics;

import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import io.github.hadron13.petrochem.compat.kubejs.schemas.ProcessingRecipeSchema;

public interface ATNFElectrolyzingKubeSchema {

    RecipeKey<Double> KILOWATTS = NumberComponent.DOUBLE.inputKey("kilowatts").alwaysWrite();
    RecipeKey<Double> VOLTAGE = NumberComponent.DOUBLE.inputKey("voltage").alwaysWrite();

    RecipeSchema ELECTROLYZING_ELECTRIC_SCHEMA = new RecipeSchema(
            ProcessingRecipeSchema.RESULTS,
            ProcessingRecipeSchema.INGREDIENTS,
            KILOWATTS,
            VOLTAGE,
            ProcessingRecipeSchema.HEAT_REQUIREMENT,
            ProcessingRecipeSchema.PROCESSING_TIME
    ).factory(ProcessingRecipeSchema.ELECTROLYZING_FACTORY);
}
