package io.github.kosianodangoo.everythingcompressed.common.block.entity;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import io.github.kosianodangoo.everythingcompressed.utils.CompressionInfoUtil;
import io.github.kosianodangoo.everythingcompressed.utils.MathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EverythingCompressorBlockEntity extends BlockEntity implements MenuProvider {
    public static final String PROGRESS_NBT = "progress";
    public static final String COMPRESSED_STACK_NBT = "compressed_stack";
    public static final String OUTPUT_NBT = "output";
    public static final String LOCKED_NBT = "locked";

    public final ContainerData data;

    private static ForgeConfigSpec.LongValue SINGULARITY_DENSITY = EverythingCompressedConfig.SINGULARITY_DENSITY;

    public CompressorItemInputHandler inputHandler = new CompressorItemInputHandler();
    public ItemStackHandler internalOutputHandler = new ItemStackHandler(1);
    public IItemHandlerModifiable outputHandler = new CombinedInvWrapper(internalOutputHandler) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }
    };
    public CombinedInvWrapper combinedInv = new CombinedInvWrapper(inputHandler, outputHandler);

    public LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> combinedInv);

    private ItemStack compressedStack = ItemStack.EMPTY;
    private long progress = 0;

    private boolean isLocked = false;

    public EverythingCompressorBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> (int) (progress & (long) -1);
                    case 1 -> (int) (progress >> Integer.SIZE & (long) -1);
                    case 2 -> (int) (getSingularityDensity() & (long) -1);
                    case 3 -> (int) (getSingularityDensity() >> Integer.SIZE & (long) -1);
                    default -> 0;
                };
            }
            @Override
            public void set(int i, int i1) {
            }
            @Override
            public int getCount() {
                return 4;
            }
        };
    }


    public EverythingCompressorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        this(ModBlockEntityTypes.EVERYTHING_COMPRESSOR.get(), p_155229_, p_155230_);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        processCompressing();
        if (progress <= 0 && !isLocked() && outputHandler.getStackInSlot(0).isEmpty()) {
            setCompressedStack(ItemStack.EMPTY);
        }
    }

    public void dropInventory(Level pLevel, BlockPos pPos) {
        Containers.dropContents(pLevel, pPos, NonNullList.of(ItemStack.EMPTY, this.outputHandler.getStackInSlot(0)));
        outputHandler.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void processCompressing() {
        if (getCompressedStack().isEmpty()) {
            return;
        }
        long singularityDensity = this.getSingularityDensity();
        if (progress >= singularityDensity) {
            long singularityCount = progress / singularityDensity;
            int remainSlot = internalOutputHandler.getSlotLimit(0) - internalOutputHandler.getStackInSlot(0).getCount();
            int actualSingularityCount = Math.min((int)singularityCount, remainSlot);
            ItemStack singularityStack = SingularityItem.fromSourceStack(this.getCompressedStack(), actualSingularityCount);
            ItemStack overflowedStack = internalOutputHandler.insertItem(0, singularityStack, false);
            progress -= (actualSingularityCount - overflowedStack.getCount()) * singularityDensity;
        }
    }

    public long getSingularityDensity() {
        return SINGULARITY_DENSITY.get();
    }

    public ItemStack getCompressedStack() {
        return compressedStack;
    }

    public void setCompressedStack(ItemStack stack) {
        this.compressedStack = stack;
    }

    public boolean isValidStack(ItemStack stack) {
        ICompressionInfo compressionInfo = CompressionInfoUtil.getCompressionInfo(stack);
        return (getCompressedStack().isEmpty() && (compressionInfo == null || compressionInfo.getCompressionTime() < Long.MAX_VALUE)) || (getProgress() < Long.MAX_VALUE &&  ItemStack.isSameItemSameTags(getCompressedStack(), stack));
    }

    public long getProgress() {
        return progress;
    }

    public IItemHandlerModifiable getInventory() {
        return this.combinedInv;
    }

    public void addStack(ItemStack stack, long count) {
        if (!isValidStack(stack)) {
            return;
        }
        if (getCompressedStack().isEmpty()) {
            setCompressedStack(stack.copyWithCount(1));
        }
        progress = MathUtil.overflowingAdd(progress, count);
    }

    public void addStack(ItemStack stack) {
        addStack(stack, stack.getCount());
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putLong(PROGRESS_NBT, getProgress());
        compoundTag.put(COMPRESSED_STACK_NBT, this.getCompressedStack().serializeNBT());
        compoundTag.put(OUTPUT_NBT, this.internalOutputHandler.serializeNBT());
        compoundTag.putBoolean(LOCKED_NBT, this.isLocked());
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        this.progress = compoundTag.getLong(PROGRESS_NBT);
        this.setCompressedStack(ItemStack.of(compoundTag.getCompound(COMPRESSED_STACK_NBT)));
        this.internalOutputHandler.deserializeNBT(compoundTag.getCompound(OUTPUT_NBT));
        this.setLocked(compoundTag.getBoolean(LOCKED_NBT));
    }

    @Override
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new EverythingCompressorMenu(i, inventory, this, this.data);
    }

    public class CompressorItemInputHandler implements IItemHandlerModifiable {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean simulate) {
            if (!this.isItemValid(i, itemStack)) {
                return itemStack;
            }
            if (!simulate && !itemStack.isEmpty()) {
                addStack(itemStack);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int i) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            return isValidStack(itemStack);
        }

        @Override
        public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
            EverythingCompressorBlockEntity.this.addStack(itemStack);
        }
    }
}
