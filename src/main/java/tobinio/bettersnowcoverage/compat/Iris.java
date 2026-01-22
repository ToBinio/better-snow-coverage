package tobinio.bettersnowcoverage.compat;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.vertices.sodium.terrain.VertexEncoderInterface;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * Created: 22.01.26
 *
 * @author Tobias Frischmann
 */
public class Iris {
    public static void setBlockData(BlockRenderer blockRenderer, BlockState blockState, BlockPos blockPos) {
        if (WorldRenderingSettings.INSTANCE.getBlockStateIds() != null) {
            ((VertexEncoderInterface) blockRenderer).beginBlock(WorldRenderingSettings.INSTANCE.getBlockStateIds().getOrDefault(blockState, -1), (byte) 0, (byte) blockState.getLuminance(), blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }
}
