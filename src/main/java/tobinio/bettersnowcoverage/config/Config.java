package tobinio.bettersnowcoverage.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.text2speech.Narrator.LOGGER;
import static tobinio.bettersnowcoverage.BetterSnowCoverage.MOD_ID;
import static tobinio.bettersnowcoverage.BetterSnowCoverage.id;

/**
 * Created: 02.08.24
 *
 * @author Tobias Frischmann
 */
public class Config {
    public static List<String> DEFAULT_EXCLUDED_BLOCKS = List.of("light_weighted_pressure_plate",
            "heavy_weighted_pressure_plate",
            "polished_blackstone_pressure_plate",
            "stone_pressure_plate");
    public static List<String> DEFAULT_EXCLUDED_TAGS = List.of("buttons", "wooden_pressure_plates", "rails", "leaves");

    public static List<Block> EXCLUDED_BLOCKS = new ArrayList<>();
    public static List<TagKey<Block>> EXCLUDED_TAGS = new ArrayList<>();

    public static void update() {
        setBlocks(HANDLER.instance().excludedBlocks, EXCLUDED_BLOCKS);
        setTags(HANDLER.instance().excludedTags, EXCLUDED_TAGS);
    }

    private static void setTags(List<String> strings, List<TagKey<Block>> tags) {
        tags.clear();

        for (String id : strings) {
            Identifier identifier = Identifier.tryParse(id);

            if (identifier == null) {
                LOGGER.error("Could not parse tag id {}", id);
                continue;
            }

            var tagKey = TagKey.of(RegistryKeys.BLOCK, identifier);
            tags.add(tagKey);
        }
    }

    private static void setBlocks(List<String> strings, List<Block> blocks) {
        blocks.clear();

        for (String id : strings) {
            Identifier identifier = Identifier.tryParse(id);

            if (identifier == null) {
                LOGGER.error("Could not parse block id {}", id);
                continue;
            }

            var block = Registries.BLOCK.getOptionalValue(identifier);

            if (block.isPresent()) {
                blocks.add(block.get());
            } else {
                LOGGER.error("Could not find block {}", id);
            }
        }
    }

    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(id("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("%s.json5".formatted(MOD_ID)))
                    .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
                    .setJson5(true)
                    .build())
            .build();

    @SerialEntry
    public List<String> excludedBlocks = new ArrayList<>(DEFAULT_EXCLUDED_BLOCKS);

    @SerialEntry
    public List<String> excludedTags = new ArrayList<>(DEFAULT_EXCLUDED_TAGS);

    @SerialEntry
    public Integer maxVerticalDistance = 2;

    @SerialEntry
    public Integer maxHorizontalDistance = 8;

    @SerialEntry
    public CheckerMode checkerMode = CheckerMode.PREFER_SNOW;

    public enum CheckerMode implements NameableEnum {
        PREFER_SNOW,
        PREFER_AIR,
        ALL_SIDES;

        @Override
        public Text getDisplayName() {
            return Text.translatable("%s.mode.%s".formatted(MOD_ID, name().toLowerCase()));
        }
    }
}
