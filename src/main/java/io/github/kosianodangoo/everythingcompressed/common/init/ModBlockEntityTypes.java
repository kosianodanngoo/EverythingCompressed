package io.github.kosianodangoo.everythingcompressed.common.init;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.block.entity.EverythingCompressorBlockEntity;
import io.github.kosianodangoo.everythingcompressed.common.block.entity.SingularityExtractorBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EverythingCompressed.MOD_ID);

    public static RegistryObject<BlockEntityType<EverythingCompressorBlockEntity>> EVERYTHING_COMPRESSOR = TILE_ENTITIES.register(
            "everything_compressor",
            () -> BlockEntityType.Builder.of(EverythingCompressorBlockEntity::new, ModBlocks.EVERYTHING_COMPRESSOR.get()).build(null)
    );
    public static RegistryObject<BlockEntityType<SingularityExtractorBlockEntity>> SINGULARITY_EXTRACTOR = TILE_ENTITIES.register(
            "singularity_extractor",
            () -> BlockEntityType.Builder.of(SingularityExtractorBlockEntity::new, ModBlocks.SINGULARITY_EXTRACTOR.get()).build(null)
    );

    public static void register(IEventBus modEventBus) {
        TILE_ENTITIES.register(modEventBus);
    }
}
