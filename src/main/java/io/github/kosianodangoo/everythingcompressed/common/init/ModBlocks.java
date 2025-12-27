package io.github.kosianodangoo.everythingcompressed.common.init;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.block.EverythingCompressorBlock;
import io.github.kosianodangoo.everythingcompressed.common.block.SingularityExtractorBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EverythingCompressed.MOD_ID);

    public static RegistryObject<Block> EVERYTHING_COMPRESSOR = BLOCKS.register("everything_compressor", EverythingCompressorBlock::new);
    public static RegistryObject<Block> SINGULARITY_EXTRACTOR = BLOCKS.register("singularity_extractor", SingularityExtractorBlock::new);

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
