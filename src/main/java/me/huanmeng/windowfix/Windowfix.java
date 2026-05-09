package me.huanmeng.windowfix;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.util.Locale;

/*? if fabric {*/
/*import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
*//*?}*/

/*? if forge {*/

/*import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;
*//*?}*/
/*? if forge && >= 1.21.6 {*/
/*import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLLoader;
*//*?}*/

/*? if neoforge && <1.20.5 {*/

/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
*//*?}*/

/*? if neoforge && >=1.20.5 {*/

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
/*?}*/

/*? if forgeLike {*/
@Mod(Windowfix.MODID)
 /*?}*/
/*? if fabric {*/
/*public class Windowfix implements ClientModInitializer {
*//*?} else {*/
    public class Windowfix {
     /*?}*/

    public static final String MODID = "windowfix";
    static final Logger LOGGER = LogUtils.getLogger();

    /*? if forge {*/

    /*public Windowfix() {
        if (!isWindows()) return;
        //noinspection removal
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WindowfixConfig.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            /^? if <= 1.21.5 { ^/
              /^MinecraftForge.EVENT_BUS.addListener(Windowfix::onClientTick);
            ^//^?} else {^/
            TickEvent.ClientTickEvent.Post.BUS.addListener(this::onClientTick);
            /^?}^/
        }
    }

    /^? if <= 1.21.5 {^/
    /^private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            WindowMessageFix.tryInstall();
        }
    }
    ^//^?} else {^/
    public void onClientTick(TickEvent.ClientTickEvent.Post event) {
        WindowMessageFix.tryInstall();
    }
    /^?}^/
    *//*?}*/

    /*? if neoforge && <1.20.5 {*/
    
    /*public Windowfix(IEventBus modEventBus) {
        if (!isWindows()) return;
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, WindowfixConfig.SPEC);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(Windowfix::onClientTick);
        }
    }

    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            WindowMessageFix.tryInstall();
        }
    }
    *//*?}*/

    /*? if neoforge && >=1.20.5 {*/
    
    public Windowfix(IEventBus modEventBus, ModContainer modContainer) {
        if (!isWindows()) return;
        modContainer.registerConfig(ModConfig.Type.CLIENT, WindowfixConfig.SPEC);
        /*? if neoforge && <= 1.21.8 {*/
        /*if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(Windowfix::onClientTick);
        }*/
        /*?} else {*/
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(Windowfix::onClientTick);
        }
        /*?}*/
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        WindowMessageFix.tryInstall();
    }
    /*?}*/

    /*? if fabric {*/
    /*@Override
    public void onInitializeClient() {
        if (!isWindows()) return;
        WindowfixConfig.load();
        ClientTickEvents.END_CLIENT_TICK.register(Windowfix::onClientTick);
    }

    private static void onClientTick(Minecraft e) {
        WindowMessageFix.tryInstall();
    }

    *//*?}*/

    public static boolean isWindows() {
        String osName = System.getProperty("os.name", "");
        return osName.toLowerCase(Locale.ROOT).contains("win");
    }
}
