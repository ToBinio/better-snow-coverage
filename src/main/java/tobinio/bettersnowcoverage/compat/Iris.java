package tobinio.bettersnowcoverage.compat;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

/**
 * Created: 22.01.26
 *
 * @author Tobias Frischmann
 */
public class Iris {
    public static void setBlockData(BlockRenderer blockRenderer, BlockState blockState, BlockPos blockPos) {
        if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
            ((VertexEncoderInterface) blockRenderer).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getOrDefault(blockState, -1), (byte) 0, (byte) blockState.getLightEmission(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }
}
