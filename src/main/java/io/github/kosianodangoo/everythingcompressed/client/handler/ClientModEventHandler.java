package io.github.kosianodangoo.everythingcompressed.client.handler;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.client.screen.EverythingCompressorScreen;
import io.github.kosianodangoo.everythingcompressed.common.init.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EverythingCompressed.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> MenuScreens.register(ModMenuTypes.EVERYTHING_COMPRESSOR.get(), EverythingCompressorScreen::new));
    }
}
