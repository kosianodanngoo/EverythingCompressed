package io.github.kosianodangoo.everythingcompressed;

import com.mojang.logging.LogUtils;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlocks;
import io.github.kosianodangoo.everythingcompressed.common.init.ModItems;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import io.github.kosianodangoo.everythingcompressed.common.init.ModMenuTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EverythingCompressed.MOD_ID)
public class EverythingCompressed {

    public static final String MOD_ID = "everything_compressed";

    public static final Logger LOGGER = LogUtils.getLogger();

    public EverythingCompressed() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntityTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EverythingCompressedConfig.SPEC);
    }
}
