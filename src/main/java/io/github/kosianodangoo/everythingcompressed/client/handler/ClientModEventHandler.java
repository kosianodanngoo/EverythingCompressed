package io.github.kosianodangoo.everythingcompressed.client.handler;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.client.screen.EverythingCompressorScreen;
import io.github.kosianodangoo.everythingcompressed.client.screen.SingularityExtractorScreen;
import io.github.kosianodangoo.everythingcompressed.common.init.ModMenuTypes;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EverythingCompressed.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.EVERYTHING_COMPRESSOR.get(), EverythingCompressorScreen::new);
            MenuScreens.register(ModMenuTypes.SINGULARITY_EXTRACTOR.get(), SingularityExtractorScreen::new);
        });
    }
}
