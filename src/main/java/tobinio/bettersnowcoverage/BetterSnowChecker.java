package tobinio.bettersnowcoverage;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
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
        if (state.getBlock() instanceof SnowyDirtBlock) {
            return state.setValue(BlockStateProperties.SNOWY, true);
        }
        return state;
    }

    public enum SnowState {
        NONE, WITH_LAYER, WITHOUT_LAYER,
    }

    public static SnowState shouldHaveSnowAboveBlock(BlockGetter world, BlockPos pos) {
        var above = pos.above();
        var state = world.getBlockState(above);

        if (state.isFaceSturdy(world,
                above,
                Direction.DOWN) || state.is(BlockTags.AIR) || state.getBlock() == Blocks.WATER) {
            return SnowState.NONE;
        }

        if (!world.getBlockState(pos).isCollisionShapeFullBlock(world, pos)) {
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
            if (state.is(tag)) {
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

    private static boolean hasSnowNeighbor(BlockGetter world, BlockPos pos) {
        var directionCheckers = new ArrayList<DirectionChecker>();
        directionCheckers.add(new DirectionChecker(Direction.NORTH, pos.below()));
        directionCheckers.add(new DirectionChecker(Direction.EAST, pos.below()));
        directionCheckers.add(new DirectionChecker(Direction.SOUTH, pos.below()));
        directionCheckers.add(new DirectionChecker(Direction.WEST, pos.below()));

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

        public Optional<Result> next(BlockGetter world, Config config) {
            lastPos = lastPos.offset(direction.getStepX(), 0, direction.getStepZ());

            var maxDepth = config.maxVerticalDistance;
            while (!world.getBlockState(lastPos).isCollisionShapeFullBlock(world, lastPos)) {
                if (maxDepth-- <= 0) {
                    return Optional.of(Result.UNDEFINED);
                }

                lastPos = lastPos.below();
            }

            while (world.getBlockState(lastPos.above()).isCollisionShapeFullBlock(world, lastPos.above())) {
                if (maxDepth-- <= 0) {
                    return Optional.of(Result.UNDEFINED);
                }

                lastPos = lastPos.above();
            }

            if (world.getBlockState(lastPos.above()).is(BlockTags.SNOW)) {
                return Optional.of(Result.SNOW);
            }

            if (world.getBlockState(lastPos.above()).is(BlockTags.AIR)) {
                return Optional.of(Result.NONE);
            }

            return Optional.empty();
        }

        enum Result {
            NONE, SNOW, UNDEFINED
        }
    }
}
