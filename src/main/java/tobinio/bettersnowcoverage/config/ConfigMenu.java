package tobinio.bettersnowcoverage.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.text.Text;

/**
 * Created: 02.08.24
 *
 * @author Tobias Frischmann
 */

public class ConfigMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Better Snow Coverage"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Better Snow Coverage"))
                        .tooltip(Text.literal("All the settings"))
                        .option(ListOption.<String>createBuilder()
                                .name(Text.literal("Excluded Blocks"))
                                .description(OptionDescription.of(Text.literal("These blocks will not render a snowLayer.")))
                                .binding(Config.DEFAULT_EXCLUDED_BLOCKS, () -> Config.HANDLER.instance().excluded_blocks, newVal -> Config.HANDLER.instance().excluded_blocks = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .option(ListOption.<String>createBuilder()
                                .name(Text.literal("Excluded Tags"))
                                .description(OptionDescription.of(Text.literal("All blocks within these tags will not render a snowLayer.")))
                                .binding(Config.DEFAULT_EXCLUDED_TAGS, () -> Config.HANDLER.instance().excluded_tags, newVal -> Config.HANDLER.instance().excluded_tags = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .build())
                .save(() -> {
                    Config.HANDLER.save();
                    Config.update();
                })
                .build()
                .generateScreen(parentScreen);
    }
}