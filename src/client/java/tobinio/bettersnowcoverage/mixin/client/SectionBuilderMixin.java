package tobinio.bettersnowcoverage.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

    @Inject (method = "build", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"))
    private void render(ChunkSectionPos sectionPos, ChunkRendererRegion renderRegion, VertexSorter vertexSorter,
            BlockBufferAllocatorStorage allocatorStorage, CallbackInfoReturnable<SectionBuilder.RenderData> cir,
            @Local BlockState blockState, @Local (ordinal = 2) BlockPos blockPos, @Local MatrixStack matrixStack,
            @Local BufferBuilder bufferBuilder, @Local Random random) {
        if (BetterSnowChecker.shouldHaveSnow(blockState, blockPos, renderRegion)) {
            blockRenderManager.renderBlock(Blocks.SNOW.getDefaultState(), blockPos, renderRegion, matrixStack, bufferBuilder, true, random);
        }
    }
}
