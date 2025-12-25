package io.github.kosianodangoo.everythingcompressed.common.menu;

import io.github.kosianodangoo.everythingcompressed.common.block.entity.EverythingCompressorBlockEntity;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import io.github.kosianodangoo.everythingcompressed.common.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class EverythingCompressorMenu extends AbstractContainerMenu {
    public static final int PLAYER_HOTBAR_SLOTS = 9;
    public static final int PLAYER_INVENTORY_ROWS = 3;
    public static final int PLAYER_INVENTORY_COLUMNS = 9;
    public static final int PLAYER_SLOTS = PLAYER_HOTBAR_SLOTS+PLAYER_INVENTORY_COLUMNS*PLAYER_INVENTORY_ROWS;

    public static final int CONTAINER_SLOTS = 2;

    public final EverythingCompressorBlockEntity blockEntity;
    public final ContainerData data;

    public EverythingCompressorMenu(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        this(pContainerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos(), ModBlockEntityTypes.EVERYTHING_COMPRESSOR.get()).orElseThrow(), new SimpleContainerData(1));
    }

    public EverythingCompressorMenu(int pContainerId, Inventory inventory, EverythingCompressorBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.EVERYTHING_COMPRESSOR.get(), pContainerId);
        this.blockEntity = blockEntity;
        this.data = data;

        addSlot(new SlotItemHandler(blockEntity.getInventory(), 0, 44, 47));
        addSlot(new SlotItemHandler(blockEntity.getInventory(), 1, 116, 47));

        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);

        addDataSlots(data);
    }

    public long getProgress() {
        return (long) data.get(0) | (long) data.get(1) << 32;
    }


    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        ItemStack stack1 = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);
        if (slot.hasItem()) {
            ItemStack stack2 = slot.getItem();
            stack1 = stack2.copy();
            if (i < CONTAINER_SLOTS) {
                if (!this.moveItemStackTo(stack2, CONTAINER_SLOTS, this.slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack2, 0, CONTAINER_SLOTS, true)) {
                return ItemStack.EMPTY;
            }

            if (stack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack1;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int i1, int i2, boolean b) {
        boolean flag = false;
        int i = i1;
        if (b) {
            i = i2 - 1;
        }

        Slot slot1;
        ItemStack itemstack;
        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (b) {
                    if (i < i1) {
                        break;
                    }
                } else if (i >= i2) {
                    break;
                }

                slot1 = this.slots.get(i);
                itemstack = slot1.getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack) && slot1.mayPlace(itemstack)) {
                    long itemStackCount = itemstack.getCount();
                    long stackCount = stack.getCount();

                    long j = itemStackCount + stackCount;
                    int maxSize = stack.getMaxStackSize();
                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount((int) j);
                        slot1.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot1.setChanged();
                        flag = true;
                    }
                }

                if (b) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (b) {
                i = i2 - 1;
            } else {
                i = i1;
            }

            while(true) {
                if (b) {
                    if (i < i1) {
                        break;
                    }
                } else if (i >= i2) {
                    break;
                }

                slot1 = this.slots.get(i);
                itemstack = slot1.getItem();
                if (itemstack.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(stack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (b) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < PLAYER_HOTBAR_SLOTS; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < PLAYER_INVENTORY_ROWS; ++i) {
            for (int l = 0; l < PLAYER_INVENTORY_COLUMNS; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
}
