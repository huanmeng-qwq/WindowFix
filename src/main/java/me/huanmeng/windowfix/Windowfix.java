package me.huanmeng.windowfix;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
import java.util.Locale;
import org.slf4j.Logger;

@Mod(Windowfix.MODID)
public class Windowfix {

    public static final String MODID = "windowfix";
    static final Logger LOGGER = LogUtils.getLogger();

    public Windowfix() {
        if (!isWindows()) {
            return;
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WindowfixConfig.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(Windowfix::onClientTick);
        }
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            WindowMessageFix.tryInstall();
        }
    }

    public static boolean isWindows() {
        String osName = System.getProperty("os.name", "");
        return osName.toLowerCase(Locale.ROOT).contains("win");
    }
}
