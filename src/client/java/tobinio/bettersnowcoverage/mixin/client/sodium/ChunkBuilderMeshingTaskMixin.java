package tobinio.bettersnowcoverage.mixin.client.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import me.jellysquid.mods.sodium.client.world.WorldSlice;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tobinio.bettersnowcoverage.BetterSnowChecker;

/**
 * Created: 31.07.24
 *
 * @author Tobias Frischmann
 */
@Mixin (value = ChunkBuilderMeshingTask.class, priority = 990)
public class ChunkBuilderMeshingTaskMixin {
    @Inject (method = "execute(Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;)Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;", at = @At (value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V", remap = false), remap = false)
    private void execute(ChunkBuildContext buildContext, CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir, @Local BlockRenderContext ctx,
            @Local ChunkBuildBuffers buffers, @Local BlockRenderCache cache, @Local WorldSlice slice,
            @Local (ordinal = 1) BlockPos.Mutable modelOffset) {
        BetterSnowChecker.SnowState snowState = BetterSnowChecker.shouldHaveSnow(ctx.pos().up(), ctx.world());

        if (snowState != BetterSnowChecker.SnowState.NONE) {
            var newState = BetterSnowChecker.getSnowState(ctx.state());
            ctx.update(ctx.pos(), modelOffset, newState, cache.getBlockModels().getModel(newState), ctx.seed());
        }

        if (snowState == BetterSnowChecker.SnowState.WITH_LAYER) {
            BlockRenderContext context = new BlockRenderContext(slice);

            BlockState snow = Blocks.SNOW.getDefaultState();
            var model = cache.getBlockModels().getModel(snow);

            context.update(ctx.pos().up(), modelOffset.up(), snow, model, ctx.seed());

            cache.getBlockRenderer().renderModel(context, buffers);
        }
    }
}
