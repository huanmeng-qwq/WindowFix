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
    private static final ForgeConfigSpec.BooleanValue KEEP_ACTIVE_DURING_SYSTEM_MENU;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BLOCK_TITLEBAR_SYSTEM_MENU = builder
            .comment(
                "true: Block title-bar right-click system menu (most stable workaround).",
                "false: Keep system menu enabled and use native modeless-menu handling.")
            .define("blockTitleBarSystemMenu", false);
        KEEP_ACTIVE_DURING_SYSTEM_MENU = builder
            .comment(
                "Only used when blockTitleBarSystemMenu = false.",
                "Try to suppress transient focus-loss messages while the system menu is open.")
            .define("keepActiveDuringSystemMenu", true);
        SPEC = builder.build();
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return BLOCK_TITLEBAR_SYSTEM_MENU.get();
    }

    static boolean shouldKeepActiveDuringSystemMenu() {
        return KEEP_ACTIVE_DURING_SYSTEM_MENU.get();
    }
    *//*?}*/

    /*? if neoforge {*/
    
    static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue BLOCK_TITLEBAR_SYSTEM_MENU;
    private static final ModConfigSpec.BooleanValue KEEP_ACTIVE_DURING_SYSTEM_MENU;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        BLOCK_TITLEBAR_SYSTEM_MENU = builder
            .comment(
                "true: Block title-bar right-click system menu (most stable workaround).",
                "false: Keep system menu enabled and use native modeless-menu handling.")
            .define("blockTitleBarSystemMenu", false);
        KEEP_ACTIVE_DURING_SYSTEM_MENU = builder
            .comment(
                "Only used when blockTitleBarSystemMenu = false.",
                "Try to suppress transient focus-loss messages while the system menu is open.")
            .define("keepActiveDuringSystemMenu", true);
        SPEC = builder.build();
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return BLOCK_TITLEBAR_SYSTEM_MENU.get();
    }

    static boolean shouldKeepActiveDuringSystemMenu() {
        return KEEP_ACTIVE_DURING_SYSTEM_MENU.get();
    }
    /*?}*/

    /*? if fabric {*/
    
    /*private static boolean blockTitleBarSystemMenu = false;
    private static boolean keepActiveDuringSystemMenu = true;
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
                if (json != null && json.has("keepActiveDuringSystemMenu")) {
                    keepActiveDuringSystemMenu = json.get("keepActiveDuringSystemMenu").getAsBoolean();
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
        json.addProperty("keepActiveDuringSystemMenu", keepActiveDuringSystemMenu);
        try (Writer writer = Files.newBufferedWriter(configFile)) {
            GSON.toJson(json, writer);
        } catch (IOException e) {
            Windowfix.LOGGER.warn("Failed to write config.", e);
        }
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return blockTitleBarSystemMenu;
    }

    static boolean shouldKeepActiveDuringSystemMenu() {
        return keepActiveDuringSystemMenu;
    }
    *//*?}*/

    private WindowfixConfig() {
    }
}
