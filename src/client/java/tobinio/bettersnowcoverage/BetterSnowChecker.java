package tobinio.bettersnowcoverage;

import net.minecraft.block.BlockState;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.SnowBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

/**
 * Created: 02.08.24
 *
 * @author Tobias Frischmann
 */
public class BetterSnowChecker {
    public static boolean shouldHaveSnow(BlockState state, BlockPos pos, BlockRenderView world) {
        if (!world.getBlockState(pos.down()).isIn(BlockTags.DIRT)) {
            return false;
        }

        if (!hasSnowNeighbor(pos, world)) {
            return false;
        }

        return state.isIn(BlockTags.FLOWERS);
    }

    public static boolean hasSnowNeighbor(BlockPos pos, BlockRenderView world) {
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
