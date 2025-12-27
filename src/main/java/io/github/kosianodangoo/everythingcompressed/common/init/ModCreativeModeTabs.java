package io.github.kosianodangoo.everythingcompressed.common.init;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EverythingCompressed.MOD_ID);
    public static List<RegistryObject<Item>> ITEMS = new ArrayList<>();


    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = TABS.register("everything_compressed", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.everything_compressed"))
            .icon(() -> SingularityItem.fromSourceStack(new ItemStack(Items.NETHER_STAR)))
            .displayItems((parameters, output) -> {
                for (var item : ITEMS) {
                    output.accept(item.get());
                }
            })
            .build());


    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
