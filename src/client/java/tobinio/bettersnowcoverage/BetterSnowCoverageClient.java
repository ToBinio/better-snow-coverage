package tobinio.bettersnowcoverage;

import net.fabricmc.api.ClientModInitializer;
import tobinio.bettersnowcoverage.config.Config;

public class BetterSnowCoverageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Config.HANDLER.load();
        Config.update();
    }
}