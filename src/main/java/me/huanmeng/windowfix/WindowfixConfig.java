package me.huanmeng.windowfix;

import net.minecraftforge.common.ForgeConfigSpec;

final class WindowfixConfig {
    static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.BooleanValue BLOCK_TITLEBAR_SYSTEM_MENU;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        BLOCK_TITLEBAR_SYSTEM_MENU = builder
            .comment(
                "true: Block title-bar right-click system menu (most stable workaround).",
                "false: Keep system menu enabled and use native modeless-menu handling.")
            .define("blockTitleBarSystemMenu", true);
        SPEC = builder.build();
    }

    private WindowfixConfig() {
    }

    static boolean shouldBlockTitlebarSystemMenu() {
        return BLOCK_TITLEBAR_SYSTEM_MENU.get();
    }
}
