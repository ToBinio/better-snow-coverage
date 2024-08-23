package tobinio.bettersnowcoverage.mixin.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderCache;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.util.task.CancellationToken;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
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
    @Inject (method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;", at = @At (value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderCache;getBlockModels()Lnet/minecraft/client/render/block/BlockModels;", remap = false), remap = false)
    private void execute(ChunkBuildContext buildContext, CancellationToken cancellationToken,
            CallbackInfoReturnable<ChunkBuildOutput> cir, @Local BlockRenderCache cache, @Local LevelSlice slice,
            @Local (ordinal = 0) BlockPos.Mutable pos, @Local (ordinal = 1) BlockPos.Mutable modelOffset,
            @Local LocalRef<BlockState> blockState) {
        BetterSnowChecker.SnowState snowState = BetterSnowChecker.shouldHaveSnow(pos.up());


        if (snowState != BetterSnowChecker.SnowState.NONE) {
            blockState.set(BetterSnowChecker.getSnowState(blockState.get()));
        }

        if (snowState == BetterSnowChecker.SnowState.WITH_LAYER) {
            BlockState snow = Blocks.SNOW.getDefaultState();
            var model = cache.getBlockModels().getModel(snow);

            cache.getBlockRenderer().renderModel(model, snow, pos.up(), modelOffset.up());
        }
    }
}
