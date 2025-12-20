package io.github.kosianodangoo.everythingcompressed.common.handler;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressed;
import io.github.kosianodangoo.everythingcompressed.common.capability.CompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EverythingCompressed.MOD_ID)
public class CommonForgeEventHandler {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("testttttt").executes((ctx) -> {
                    ctx.getSource().getPlayer().addItem(SingularityItem.fromCompressionInfo(new CompressionInfo(new ItemStack(Items.DIAMOND))));
                    ctx.getSource().getPlayer().addItem(SingularityItem.fromCompressionInfo(new CompressionInfo(new ItemStack(Items.DIAMOND), Long.MAX_VALUE)));
                    ctx.getSource().getPlayer().addItem(SingularityItem.fromCompressionInfo(new CompressionInfo(new ItemStack(Items.DIAMOND), Integer.MAX_VALUE)));
                    ctx.getSource().sendSuccess(() -> Component.literal("succeed"), false);
                    return 0;
                })
        );
    }
}
