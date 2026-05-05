package me.huanmeng.windowfix;

import net.minecraftforge.common.ForgeConfigSpec;

final class WindowfixConfig {
    static final ForgeConfigSpec SPEC;
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
                "Try to suppress transient focus-loss messages while the system menu is open",
                "to reduce the brief game hitch when right-clicking the title bar.")
            .define("keepActiveDuringSystemMenu", true);
        SPEC = builder.build();
    }

    private WindowfixConfig() {
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return BLOCK_TITLEBAR_SYSTEM_MENU.get();
    }

    static boolean shouldKeepActiveDuringSystemMenu() {
        return KEEP_ACTIVE_DURING_SYSTEM_MENU.get();
    }
}
