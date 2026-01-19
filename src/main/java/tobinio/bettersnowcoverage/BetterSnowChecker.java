package tobinio.bettersnowcoverage;

import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import tobinio.bettersnowcoverage.config.Config;

import java.util.ArrayList;
import java.util.Optional;

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

    public static SnowState shouldHaveSnowAboveBlock(BlockView world, BlockPos pos) {
        var above = pos.up();
        var state = world.getBlockState(above);

        if (state.isSideSolidFullSquare(world,
                above,
                Direction.DOWN) || state.isIn(BlockTags.AIR) || state.getBlock() == Blocks.WATER) {
            return SnowState.NONE;
        }

        if (!world.getBlockState(pos).isFullCube(world, pos)) {
            return SnowState.NONE;
        }

        if (hasSnowNeighbor(world, above)) {
            Config config = Config.HANDLER.instance();
            if (isInBlockList(state) == (config.listMode == Config.ListMode.BLACK_LIST)) {
                return SnowState.WITHOUT_LAYER;
            }

            return SnowState.WITH_LAYER;
        }

        return SnowState.NONE;
    }

    private static boolean isInBlockList(BlockState state) {
        for (TagKey<Block> tag : Config.TAGS_LIST) {
            if (state.isIn(tag)) {
                return true;
            }
        }

        for (Block block : Config.BLOCKS_LIST) {
            if (state.getBlock().equals(block)) {
                return true;
            }
        }

        return false;
    }

    private static boolean hasSnowNeighbor(BlockView world, BlockPos pos) {
        var directionCheckers = new ArrayList<DirectionChecker>();
        directionCheckers.add(new DirectionChecker(Direction.NORTH, pos.down()));
        directionCheckers.add(new DirectionChecker(Direction.EAST, pos.down()));
        directionCheckers.add(new DirectionChecker(Direction.SOUTH, pos.down()));
        directionCheckers.add(new DirectionChecker(Direction.WEST, pos.down()));

        Config config = Config.HANDLER.instance();

        var snowCount = 0;
        var noneCount = 0;

        for (int j = 0; j < config.maxHorizontalDistance; j++) {
            for (int i = directionCheckers.size() - 1; i >= 0; i--) {
                var directionChecker = directionCheckers.get(i);
                Optional<DirectionChecker.Result> result = directionChecker.next(world, config);

                if (result.isEmpty()) continue;

                switch (result.get()) {
                    case NONE -> noneCount++;
                    case SNOW -> snowCount++;
                }

                directionCheckers.remove(i);
            }

            if (snowCount != 0 || noneCount != 0) {
                switch (config.checkerMode) {
                    case PREFER_SNOW -> {
                        return snowCount >= noneCount;
                    }
                    case PREFER_AIR -> {
                        return snowCount > noneCount;
                    }
                    case ALL_SIDES -> {
                        if (snowCount == 4) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    static class DirectionChecker {
        private final Direction direction;
        private BlockPos lastPos;

        public DirectionChecker(Direction direction, BlockPos pos) {
            this.direction = direction;
            this.lastPos = pos;
        }

        public Optional<Result> next(BlockView world, Config config) {
            lastPos = lastPos.add(direction.getOffsetX(), 0, direction.getOffsetZ());

            var maxDepth = config.maxVerticalDistance;
            while (!world.getBlockState(lastPos).isFullCube(world, lastPos)) {
                if (maxDepth-- <= 0) {
                    return Optional.of(Result.UNDEFINED);
                }

                lastPos = lastPos.down();
            }

            while (world.getBlockState(lastPos.up()).isFullCube(world, lastPos.up())) {
                if (maxDepth-- <= 0) {
                    return Optional.of(Result.UNDEFINED);
                }

                lastPos = lastPos.up();
            }

            if (world.getBlockState(lastPos.up()).isIn(BlockTags.SNOW)) {
                return Optional.of(Result.SNOW);
            }

            if (world.getBlockState(lastPos.up()).isIn(BlockTags.AIR)) {
                return Optional.of(Result.NONE);
            }

            return Optional.empty();
        }

        enum Result {
            NONE, SNOW, UNDEFINED
        }
    }
}
