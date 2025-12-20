package io.github.kosianodangoo.everythingcompressed.common.init;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EverythingCompressed.MOD_ID);

    public static RegistryObject<Item> SINGULARITY = ITEMS.register("singularity", () -> new SingularityItem(new Item.Properties()));
    public static RegistryObject<Item> EVERYTHING_COMPRESSOR = ITEMS.register("everything_compressor", () -> new BlockItem(ModBlocks.EVERYTHING_COMPRESSOR.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
