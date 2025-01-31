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
                .title(Text.translatable("better-snow-coverage.config.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("better-snow-coverage.config.category-title"))
                        .tooltip(Text.literal("All the settings"))
                        .option(ListOption.<String>createBuilder()
                                .name(Text.translatable("better-snow-coverage.config.excluded-blocks.name"))
                                .description(OptionDescription.of(Text.translatable(
                                        "better-snow-coverage.config.excluded-blocks.description")))
                                .binding(Config.DEFAULT_EXCLUDED_BLOCKS,
                                        () -> Config.HANDLER.instance().excludedBlocks,
                                        newVal -> Config.HANDLER.instance().excludedBlocks = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .option(ListOption.<String>createBuilder()
                                .name(Text.translatable("better-snow-coverage.config.excluded-tags.name"))
                                .description(OptionDescription.of(Text.translatable(
                                        "better-snow-coverage.config.excluded-tags.description")))
                                .binding(Config.DEFAULT_EXCLUDED_TAGS,
                                        () -> Config.HANDLER.instance().excludedTags,
                                        newVal -> Config.HANDLER.instance().excludedTags = newVal)
                                .controller(StringControllerBuilder::create)
                                .initial("")
                                .collapsed(true)
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("better-snow-coverage.config.max-vertical.name"))
                                .description(OptionDescription.of(Text.translatable(
                                        "better-snow-coverage.config.max-vertical.description")))
                                .binding(Config.DEFAULT_MAX_VERTICAL_DISTANCE,
                                        () -> Config.HANDLER.instance().maxVerticalDistance,
                                        newVal -> Config.HANDLER.instance().maxVerticalDistance = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.translatable("better-snow-coverage.config.max-horizontal.name"))
                                .description(OptionDescription.of(Text.translatable(
                                        "better-snow-coverage.config.max-horizontal.description")))
                                .binding(Config.DEFAULT_MAX_HORIZONTAL_DISTANCE,
                                        () -> Config.HANDLER.instance().maxHorizontalDistance,
                                        newVal -> Config.HANDLER.instance().maxHorizontalDistance = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                .build())
                        .option(Option.<Config.CheckerMode>createBuilder()
                                .name(Text.translatable("better-snow-coverage.config.checker-mode.name"))
                                .description(OptionDescription.of(Text.translatable(
                                        "better-snow-coverage.config.checker-mode.description")))
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