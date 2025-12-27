package io.github.kosianodangoo.everythingcompressed.common.init;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EverythingCompressed.MOD_ID);

    public static RegistryObject<Item> SINGULARITY = register("singularity", () -> new SingularityItem(new Item.Properties()), false);
    public static RegistryObject<Item> EVERYTHING_COMPRESSOR = register("everything_compressor", () -> new BlockItem(ModBlocks.EVERYTHING_COMPRESSOR.get(), new Item.Properties()), true);
    public static RegistryObject<Item> SINGULARITY_EXTRACTOR = register("singularity_extractor", () -> new BlockItem(ModBlocks.SINGULARITY_EXTRACTOR.get(), new Item.Properties()), true);

    public static RegistryObject<Item> register(String name, Supplier<Item> itemSupplier, boolean isCreativeTab) {
        RegistryObject<Item> registryObject = ITEMS.register(name, itemSupplier);
        if(isCreativeTab) {
            ModCreativeModeTabs.ITEMS.add(registryObject);
        }
        return registryObject;
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
