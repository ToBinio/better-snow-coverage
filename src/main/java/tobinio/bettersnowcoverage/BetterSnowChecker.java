package tobinio.bettersnowcoverage;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

    public static SnowState shouldHaveSnowAboveBlock(ChunkRendererRegion chunkRendererRegion, BlockPos pos) {
        var state = chunkRendererRegion.getBlockState(pos.up());

        if (state.isSideSolidFullSquare(chunkRendererRegion, pos, Direction.DOWN) || state.isAir() || state.getBlock() == Blocks.WATER) {
            return SnowState.NONE;
        }

        if (!chunkRendererRegion.getBlockState(pos.down()).isFullCube(chunkRendererRegion, pos.down())) {
            return SnowState.NONE;
        }

        if (hasSnowNeighbor(chunkRendererRegion, pos)) {
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

    private static boolean hasSnowNeighbor(ChunkRendererRegion chunkRendererRegion, BlockPos pos) {
        var directionCheckers = new ArrayList<DirectionChecker>();
        directionCheckers.add(new DirectionChecker(Direction.NORTH, pos));
        directionCheckers.add(new DirectionChecker(Direction.EAST, pos));
        directionCheckers.add(new DirectionChecker(Direction.SOUTH, pos));
        directionCheckers.add(new DirectionChecker(Direction.WEST, pos));

        var maxDepth = 8;
        for (int j = 0; j < maxDepth; j++) {

            var results = new ArrayList<Optional<DirectionChecker.Result>>();

            for (DirectionChecker directionChecker : directionCheckers) {
                Optional<DirectionChecker.Result> result = directionChecker.next(chunkRendererRegion);
                results.add(result);
            }

            var snowCount = 0;
            var noneCount = 0;

            for (int i = results.size() - 1; i >= 0; i--) {
                var result = results.get(i);

                if (result.isEmpty()) continue;

                switch (result.get()) {
                    case NONE -> noneCount++;
                    case SNOW -> snowCount++;
                    case UNDEFINED -> directionCheckers.remove(i);
                }
            }

            if (snowCount != 0 || noneCount != 0) {
                return snowCount >= noneCount;
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

        public Optional<Result> next(ChunkRendererRegion chunkRendererRegion) {
            lastPos = lastPos.add(direction.getOffsetX(), 0, direction.getOffsetZ());

            var maxDepth = 2;
            while (!chunkRendererRegion.getBlockState(lastPos).isFullCube(chunkRendererRegion, lastPos)) {
                if (maxDepth-- < 0) {
                    return Optional.of(Result.UNDEFINED);
                }

                lastPos = lastPos.down();
            }

            while (chunkRendererRegion.getBlockState(lastPos.up()).isFullCube(chunkRendererRegion, lastPos.up())) {
                if (maxDepth-- < 0) {
                    return Optional.of(Result.UNDEFINED);
                }

                lastPos = lastPos.up();
            }

            if (chunkRendererRegion.getBlockState(lastPos.up()).isIn(BlockTags.SNOW)) {
                return Optional.of(Result.SNOW);
            }

            if (chunkRendererRegion.getBlockState(lastPos.up()).isAir()) {
                return Optional.of(Result.NONE);
            }

            return Optional.empty();
        }

        enum Result {
            NONE, SNOW, UNDEFINED
        }
    }
}
