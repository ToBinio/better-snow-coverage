package tobinio.bettersnowcoverage.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
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
                .title(Text.literal("Better Snow Coverage (requires blockupdate)"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.literal("Better Snow Coverage"))
                        .tooltip(Text.literal("All the settings"))
                        .option(ListOption.<String>createBuilder()
                                .name(Text.literal("Excluded Blocks"))
                                .description(OptionDescription.of(Text.literal(
                                        "These blocks will not render a snowLayer.")))
                                .binding(Config.DEFAULT_EXCLUDED_BLOCKS,
                                        () -> Config.HANDLER.instance().excludedBlocks,
                                        newVal -> Config.HANDLER.instance().excludedBlocks = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .option(ListOption.<String>createBuilder()
                                .name(Text.literal("Excluded Tags"))
                                .description(OptionDescription.of(Text.literal(
                                        "All blocks within these tags will not render a snowLayer.")))
                                .binding(Config.DEFAULT_EXCLUDED_TAGS,
                                        () -> Config.HANDLER.instance().excludedTags,
                                        newVal -> Config.HANDLER.instance().excludedTags = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Max Vertical Distance"))
                                .description(OptionDescription.of(Text.literal(
                                        "Max vertical distance to check if snow is present.")))
                                .binding(Config.DEFAULT_MAX_VERTICAL_DISTANCE,
                                        () -> Config.HANDLER.instance().maxVerticalDistance,
                                        newVal -> Config.HANDLER.instance().maxVerticalDistance = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.literal("Max Horizontal Distance"))
                                .description(OptionDescription.of(Text.literal(
                                        "Max horizontal distance to check if snow is present.")))
                                .binding(Config.DEFAULT_MAX_HORIZONTAL_DISTANCE,
                                        () -> Config.HANDLER.instance().maxHorizontalDistance,
                                        newVal -> Config.HANDLER.instance().maxHorizontalDistance = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                .build())
                        .option(Option.<Config.CheckerMode>createBuilder()
                                .name(Text.literal("Checker Mode"))
                                .description(OptionDescription.of(Text.literal(
                                        """
                                                Prefer Snow: show snow if equal amount of snow and air is present.
                                                Prefer Air: show air if equal amount of snow and air is present.
                                                All Sides: only show Snow if snow is present on all sides.""")))
                                .binding(Config.DEFAULT_CHECKER_MODE,
                                        () -> Config.HANDLER.instance().checkerMode,
                                        newVal -> Config.HANDLER.instance().checkerMode = newVal)
                                .controller(opt -> EnumControllerBuilder.create(opt)
                                        .enumClass(Config.CheckerMode.class))
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