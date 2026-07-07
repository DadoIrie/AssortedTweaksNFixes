package com.dadoirie.assortedtweaksnfixes.mixin.controlling.newkeybindslist;

import com.blamejared.controlling.client.NewKeyBindsList;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NewKeyBindsList.KeyEntry.class)
public interface NewKeyBindsListKeyEntryAccessor {

    @Accessor("key")
    KeyMapping assortedtweaksnfixes$getBinding();
}