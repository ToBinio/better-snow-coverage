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
        return parentScreen -> YetAnotherConfigLib.create(Config.HANDLER,
                (defaults, config, builder) -> builder.title(Text.translatable("better-snow-coverage.config.title"))
                        .category(ConfigCategory.createBuilder()
                                .name(Text.translatable("better-snow-coverage.config.category-title"))
                                .tooltip(Text.literal("All the settings"))
                                .option(ListOption.<String>createBuilder()
                                        .name(Text.translatable("better-snow-coverage.config.excluded-blocks.name"))
                                        .description(OptionDescription.of(Text.translatable(
                                                "better-snow-coverage.config.excluded-blocks.description")))
                                        .binding(defaults.excludedBlocks,
                                                () -> config.excludedBlocks,
                                                newVal -> config.excludedBlocks = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .initial("")
                                        .collapsed(true)
                                        .build())
                                .option(ListOption.<String>createBuilder()
                                        .name(Text.translatable("better-snow-coverage.config.excluded-tags.name"))
                                        .description(OptionDescription.of(Text.translatable(
                                                "better-snow-coverage.config.excluded-tags.description")))
                                        .binding(defaults.excludedTags,
                                                () -> config.excludedTags,
                                                newVal -> config.excludedTags = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .initial("")
                                        .collapsed(true)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.translatable("better-snow-coverage.config.max-vertical.name"))
                                        .description(OptionDescription.of(Text.translatable(
                                                "better-snow-coverage.config.max-vertical.description")))
                                        .binding(defaults.maxVerticalDistance,
                                                () -> config.maxVerticalDistance,
                                                newVal -> config.maxVerticalDistance = newVal)
                                        .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.translatable("better-snow-coverage.config.max-horizontal.name"))
                                        .description(OptionDescription.of(Text.translatable(
                                                "better-snow-coverage.config.max-horizontal.description")))
                                        .binding(defaults.maxHorizontalDistance,
                                                () -> config.maxHorizontalDistance,
                                                newVal -> config.maxHorizontalDistance = newVal)
                                        .controller(opt -> IntegerFieldControllerBuilder.create(opt).min(1))
                                        .build())
                                .option(Option.<Config.CheckerMode>createBuilder()
                                        .name(Text.translatable("better-snow-coverage.config.checker-mode.name"))
                                        .description(OptionDescription.of(Text.translatable(
                                                "better-snow-coverage.config.checker-mode.description")))
                                        .binding(defaults.checkerMode,
                                                () -> config.checkerMode,
                                                newVal -> config.checkerMode = newVal)
                                        .controller(opt -> EnumControllerBuilder.create(opt)
                                                .enumClass(Config.CheckerMode.class))
                                        .build())
                                .build())
        ).generateScreen(parentScreen);
    }
}