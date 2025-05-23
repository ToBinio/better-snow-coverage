package tobinio.bettersnowcoverage.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferBuilderStorage;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
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
@Mixin (ChunkBuilder.BuiltChunk.RebuildTask.class)
public class ChunkBuilderMixin {

    @Inject (method = "render", at = @At (value = "INVOKE", target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"))
    private void render(float cameraX, float cameraY, float cameraZ, BlockBufferBuilderStorage storage,
            CallbackInfoReturnable<ChunkBuilder.BuiltChunk.RebuildTask.RenderData> cir,
            @Local (ordinal = 2) BlockPos blockPos, @Local MatrixStack matrixStack, @Local BufferBuilder bufferBuilder,
            @Local Random random, @Local BlockRenderManager blockRenderManager, @Local ChunkRendererRegion chunkRendererRegion) {

        var snowState = BetterSnowChecker.shouldHaveSnowAboveBlock(chunkRendererRegion, blockPos);

        if (snowState == BetterSnowChecker.SnowState.WITH_LAYER) {
            matrixStack.push();
            matrixStack.translate(0, 1, 0);
            blockRenderManager.renderBlock(Blocks.SNOW.getDefaultState(),
                    blockPos.up(),
                    chunkRendererRegion,
                    matrixStack,
                    bufferBuilder,
                    true,
                    random);
            matrixStack.pop();
        }
    }

    @ModifyVariable (method = "render", at = @At ("STORE"), ordinal = 0)
    private BlockState setGrassState(BlockState state, @Local (ordinal = 2) BlockPos blockPos) {
        var snowState = BetterSnowChecker.shouldHaveSnow(blockPos.up());

        if (snowState != BetterSnowChecker.SnowState.NONE) {
            return BetterSnowChecker.getSnowState(state);
        }

        return state;
    }
}
