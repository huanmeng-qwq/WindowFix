package me.huanmeng.windowfix;

/*? if forge {*/
/*import net.minecraftforge.common.ForgeConfigSpec;
*//*?}*/

/*? if neoforge {*/
import net.neoforged.neoforge.common.ModConfigSpec;
/*?}*/

/*? if fabric {*/

/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
*//*?}*/

final class WindowfixConfig {

    /*? if forge {*/
    /*static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.BooleanValue BLOCK_TITLEBAR_SYSTEM_MENU;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BLOCK_TITLEBAR_SYSTEM_MENU = builder
            .comment(
                "true: Block title-bar right-click system menu (most stable workaround).",
                "false: Keep system menu enabled and use native modeless-menu handling.")
            .define("blockTitleBarSystemMenu", false);
        SPEC = builder.build();
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return BLOCK_TITLEBAR_SYSTEM_MENU.get();
    }
    *//*?}*/

    /*? if neoforge {*/
    
    static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue BLOCK_TITLEBAR_SYSTEM_MENU;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        BLOCK_TITLEBAR_SYSTEM_MENU = builder
            .comment(
                "true: Block title-bar right-click system menu (most stable workaround).",
                "false: Keep system menu enabled and use native modeless-menu handling.")
            .define("blockTitleBarSystemMenu", false);
        SPEC = builder.build();
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return BLOCK_TITLEBAR_SYSTEM_MENU.get();
    }
    /*?}*/

    /*? if fabric {*/
    
    /*private static boolean blockTitleBarSystemMenu = false;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static void load() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configFile = configDir.resolve("windowfix.json");

        if (Files.exists(configFile)) {
            try (Reader reader = Files.newBufferedReader(configFile)) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                if (json != null && json.has("blockTitleBarSystemMenu")) {
                    blockTitleBarSystemMenu = json.get("blockTitleBarSystemMenu").getAsBoolean();
                }
            } catch (IOException e) {
                Windowfix.LOGGER.warn("Failed to read config, using defaults.", e);
            }
        } else {
            save(configFile);
        }
    }

    private static void save(Path configFile) {
        JsonObject json = new JsonObject();
        json.addProperty("blockTitleBarSystemMenu", blockTitleBarSystemMenu);
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            Windowfix.LOGGER.warn("Failed to write config.", e);
        }
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return blockTitleBarSystemMenu;
    }

    *//*?}*/

    private WindowfixConfig() {
    }
}
