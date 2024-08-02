package tobinio.bettersnowcoverage.mixin.client;

import net.minecraft.block.AbstractBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Created: 31.07.24
 *
 * @author Tobias Frischmann
 */
@Mixin (AbstractBlock.class)
public interface AbstractBlockAccessor {
    @Invoker
    float callGetMaxHorizontalModelOffset();
}
