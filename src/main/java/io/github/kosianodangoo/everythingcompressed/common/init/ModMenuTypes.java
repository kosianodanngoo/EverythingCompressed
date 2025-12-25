package io.github.kosianodangoo.everythingcompressed.common.init;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, EverythingCompressed.MOD_ID);

    public static final RegistryObject<MenuType<EverythingCompressorMenu>> EVERYTHING_COMPRESSOR = MENU_TYPES.register("everything_compressor", () -> IForgeMenuType.create(EverythingCompressorMenu::new));

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
