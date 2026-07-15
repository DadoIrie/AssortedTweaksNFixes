package com.dadoirie.assortedtweaksnfixes.mixin.createconnected.ccmovementbehaviours;

import net.minecraft.core.Holder;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(JukeboxSongPlayer.class)
public interface JukeboxSongPlayerAccessor {

    @Accessor("song")
    void atnf$setSong(@Nullable Holder<JukeboxSong> song);
}
