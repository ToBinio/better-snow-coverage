package tobinio.bettersnowcoverage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tobinio.bettersnowcoverage.config.Config;

import java.util.Optional;

public class BetterSnowCoverage implements ClientModInitializer {
    public static final String MOD_ID = "better-snow-coverage";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitializeClient() {
        Config.HANDLER.load();
        Config.update();

        registerBuiltinPacks();
    }

    @SuppressWarnings ("UnstableApiUsage")
    public void registerBuiltinPacks() {
        Optional<ModContainer> optionalContainer = FabricLoader.getInstance().getModContainer(MOD_ID);
        assert optionalContainer.isPresent();
        ModContainer container = optionalContainer.get();
        ResourceManagerHelperImpl.registerBuiltinResourcePack(id("z-fighting"),
                "z-fighting",
                container,
                Text.translatable(MOD_ID + "pack.z-fighting.title"),
                ResourcePackActivationType.DEFAULT_ENABLED);
    }
}