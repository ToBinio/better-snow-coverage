package tobinio.bettersnowcoverage;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import tobinio.bettersnowcoverage.config.Config;

/**
 * Created: 02.08.24
 *
 * @author Tobias Frischmann
 */
public class BetterSnowChecker {

    public static BlockState getSnowState(BlockState state) {
        if (state.getBlock() instanceof SnowyBlock) {
            return state.with(Properties.SNOWY, true);
        }
        return state;
    }

    public enum SnowState {
        NONE, WITH_LAYER, WITHOUT_LAYER,
    }

    public static SnowState shouldHaveSnow(BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world == null) {
            BetterSnowCoverage.LOGGER.warn("no player found");
            return SnowState.NONE;
        }

        var state = world.getBlockState(pos);

        if (state.isSideSolidFullSquare(world, pos, Direction.DOWN) || state.isIn(BlockTags.AIR) || state.getBlock() == Blocks.WATER) {
            return SnowState.NONE;
        }

        if (!world.getBlockState(pos.down()).isFullCube(world, pos.down())) {
            return SnowState.NONE;
        }

        if (hasSnowNeighbor(pos, world)) {
            if (isExcludedBlock(state)) {
                return SnowState.WITHOUT_LAYER;
            }

            return SnowState.WITH_LAYER;
        }

        return SnowState.NONE;
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

    private static boolean hasSnowNeighbor(BlockPos pos, ClientWorld world) {
        var directions = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

        for (Direction direction : directions) {
            if (hasSnowInDirection(pos, world, direction)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasSnowInDirection(BlockPos pos, ClientWorld world, Direction direction) {
        var maxDepth = 8;

        while (maxDepth-- > 0) {
            pos = pos.add(direction.getOffsetX(), 0, direction.getOffsetZ());

            var maxDepth2 = 4;
            while (!world.getBlockState(pos).isFullCube(world, pos)) {
                if (maxDepth2-- <= 0) {
                    return false;
                }

                pos = pos.down();
            }

            while (world.getBlockState(pos.up()).isFullCube(world, pos.up())) {
                if (maxDepth2-- <= 0) {
                    return false;
                }

                pos = pos.up();
            }

            if (world.getBlockState(pos.up()).isIn(BlockTags.SNOW)) {
                return true;
            }

            if (world.getBlockState(pos.up()).isIn(BlockTags.AIR)) {
                return false;
            }
        }

        return false;
    }
}
