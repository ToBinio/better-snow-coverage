package tobinio.bettersnowcoverage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import tobinio.bettersnowcoverage.config.Config;

import java.util.logging.Logger;

/**
 * Created: 02.08.24
 *
 * @author Tobias Frischmann
 */
public class BetterSnowChecker {
    public static boolean shouldHaveSnow(BlockState state, BlockPos pos, BlockRenderView world) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null) {
            BetterSnowCoverage.LOGGER.warn("no player found");
            return false;
        }

        if (state.isSideSolidFullSquare(world, pos, Direction.DOWN)) {
            return false;
        }

        if (!world.getBlockState(pos.down()).isFullCube(world, pos.down())) {
            return false;
        }

        if (isExcludedBlock(state)) {
            return false;
        }

        return hasSnowNeighbor(pos, world);
    }

    private static boolean isExcludedBlock(BlockState state) {
        for (TagKey<Block> tag : Config.EXCLUDED_TAGS) {
            if (state.isIn(tag)) {
                return true;
            }
        }

        for (Block block : Config.EXCLUDED_BLOCKS) {
            if (state.getBlock().equals(block)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasSnowNeighbor(BlockPos pos, BlockRenderView world) {
        var directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        for (Direction direction : directions) {
            BlockState neighbor = world.getBlockState(pos.add(direction.getOffsetX(), 0, direction.getOffsetZ()));

            if (neighbor.isIn(BlockTags.SNOW)) {
                return true;
            }
        }

        return false;
    }
}
