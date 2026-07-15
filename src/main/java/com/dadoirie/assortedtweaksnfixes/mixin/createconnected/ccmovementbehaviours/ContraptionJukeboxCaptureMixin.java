package com.dadoirie.assortedtweaksnfixes.mixin.createconnected.ccmovementbehaviours;

import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Contraption.class, priority = 1001)
public class ContraptionJukeboxCaptureMixin {

    @Inject(
            at = @At("HEAD"),
            method = "addBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lorg/apache/commons/lang3/tuple/Pair;)V"
    )
    private void atnf$defuseCreateConnectedJukeboxStop(Level level, BlockPos pos,
                                                       Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair,
                                                       CallbackInfo ci) {
        if (pair.getValue() instanceof JukeboxBlockEntity jukebox && jukebox.getSongPlayer().isPlaying()) {
            ((JukeboxSongPlayerAccessor) jukebox.getSongPlayer()).atnf$setSong(null);
        }
    }
}
