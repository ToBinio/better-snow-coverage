package tobinio.bettersnowcoverage.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.fabric.impl.client.indigo.renderer.accessor.AccessChunkRendererRegion;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tobinio.bettersnowcoverage.BetterSnowChecker;

/**
 * Created: 30.07.24
 *
 * @author Tobias Frischmann
 */
@Mixin (SectionBuilder.class)
public class SectionBuilderMixin {
    @Shadow
    @Final
    private BlockRenderManager blockRenderManager;

    @Inject (method = "build", at = @At (value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"))
    private void betterSnowCoverage$render(ChunkSectionPos sectionPos, ChunkRendererRegion renderRegion,
            VertexSorter vertexSorter,
            BlockBufferAllocatorStorage allocatorStorage, CallbackInfoReturnable<SectionBuilder.RenderData> cir,
            @Local BlockState blockState, @Local (ordinal = 2) BlockPos blockPos) {

        if (blockState.getRenderType() == BlockRenderType.MODEL && BetterSnowChecker.shouldHaveSnow(blockPos.up()) == BetterSnowChecker.SnowState.WITH_LAYER ) {
            var snowBlockPos = blockPos.up();
            var snowBlockState = Blocks.SNOW.getDefaultState();
            BlockStateModel model = blockRenderManager.getModel(snowBlockState);

            ((AccessChunkRendererRegion) renderRegion).fabric_getRenderer().bufferModel(model, snowBlockState, snowBlockPos);
        }
    }

    @ModifyVariable (method = "build", at = @At ("STORE"), ordinal = 0)
    private BlockState betterSnowCoverage$setGrassState(BlockState state, @Local (ordinal = 2) BlockPos blockPos) {
        var snowState = BetterSnowChecker.shouldHaveSnow(blockPos.up());

        if (snowState != BetterSnowChecker.SnowState.NONE) {
            return BetterSnowChecker.getSnowState(state);
        }

        return state;
    }
}
