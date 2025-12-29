package io.github.kosianodangoo.everythingcompressed.common.block.entity;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import io.github.kosianodangoo.everythingcompressed.common.item.SingularityItem;
import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import io.github.kosianodangoo.everythingcompressed.utils.CompressionInfoUtil;
import io.github.kosianodangoo.everythingcompressed.utils.EverythingMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EverythingCompressorBlockEntity extends BlockEntity implements MenuProvider {
    public static final String PROGRESS_NBT = "progress";
    public static final String COMPRESSED_STACK_NBT = "compressedStack";
    public static final String PRODUCT_AMOUNT_NBT = "productAmount";
    public static final String PRODUCT_NBT = "product";
    public static final String LOCKED_NBT = "locked";

    public final ContainerData data;

    private static ForgeConfigSpec.LongValue SINGULARITY_DENSITY = EverythingCompressedConfig.SINGULARITY_DENSITY;

    public CompressorItemInputHandler inputHandler = new CompressorItemInputHandler();
    public CompressorItemOutputHandler outputHandler = new CompressorItemOutputHandler();
    public CompressorSlotItemOutputHandler slotOutputHandler = new CompressorSlotItemOutputHandler();
    public CombinedInvWrapper combinedInv = new CombinedInvWrapper(inputHandler, outputHandler);

    public LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> combinedInv);

    private ItemStack productStack = ItemStack.EMPTY;
    private ItemStack compressedStack = ItemStack.EMPTY;
    private long progress = 0;
    private long product = 0;

    private boolean isLocked = false;

    public EverythingCompressorBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                int index = i >> 2;
                int shortindex = i & 3;
                if(index < 3) {
                    return switch (index) {
                        case 0 -> (int) (progress >> (Short.SIZE * shortindex) & EverythingMathUtil.SHORT_MASK);
                        case 1 -> (int) (getSingularityDensity() >> (Short.SIZE * shortindex) & EverythingMathUtil.SHORT_MASK);
                        case 2 -> (int) (product >> ((Short.SIZE) * shortindex) & EverythingMathUtil.SHORT_MASK);
                        default -> 0;
                    };
                }
                return switch (i) {
                    case 12 -> isLocked() ? 1 : 0;
                    default -> 0;
                };
            }
            @Override
            public void set(int i, int i1) {
                if (i == 12) {
                    setLocked(i1 == 1);
                }
            }
            @Override
            public int getCount() {
                return 13;
            }
        };
    }


    public EverythingCompressorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        this(ModBlockEntityTypes.EVERYTHING_COMPRESSOR.get(), p_155229_, p_155230_);
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        processCompressing();
        if (progress <= 0 && !isLocked() && product <= 0) {
            setCompressedStack(ItemStack.EMPTY);
            setProductStack(ItemStack.EMPTY);
        }
    }

    public void processCompressing() {
        if (getCompressedStack().isEmpty()) {
            return;
        }
        long singularityDensity = this.getSingularityDensity();
        if (progress >= singularityDensity) {
            if (getProductStack().isEmpty()) {
                setProductStack(SingularityItem.fromSourceStack(getCompressedStack()));
            }
            long singularityCount = progress / singularityDensity;
            product = EverythingMathUtil.overflowingAdd(product, singularityCount);
            progress -= singularityCount * singularityDensity;
        }
    }

    public void exportCompressedStack() {
        if(this.product > 0) {
            return;
        }
        this.setProductStack(this.getCompressedStack());
        this.product = this.getProgress();
        this.progress = 0;
        this.setCompressedStack(ItemStack.EMPTY);
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

    public ItemStack getProductStack() {
        return productStack;
    }

    public void setProductStack(ItemStack stack) {
        this.productStack = stack;
    }

    public boolean isValidStack(ItemStack stack) {
        ICompressionInfo compressionInfo = CompressionInfoUtil.getCompressionInfo(stack);
        return (getProductStack().isEmpty() && getCompressedStack().isEmpty() && (compressionInfo == null || compressionInfo.getCompressionTime() < Long.MAX_VALUE)) || (getProgress() < Long.MAX_VALUE &&  ItemStack.isSameItemSameTags(getCompressedStack(), stack));
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
        progress = EverythingMathUtil.overflowingAdd(progress, count);
        processCompressing();
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
        compoundTag.put(PRODUCT_NBT, this.getProductStack().serializeNBT());
        compoundTag.putLong(PRODUCT_AMOUNT_NBT, product);
        compoundTag.putBoolean(LOCKED_NBT, this.isLocked());
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        this.progress = compoundTag.getLong(PROGRESS_NBT);
        this.setCompressedStack(ItemStack.of(compoundTag.getCompound(COMPRESSED_STACK_NBT)));
        this.setProductStack(ItemStack.of(compoundTag.getCompound(PRODUCT_NBT)));
        this.product = compoundTag.getLong(PRODUCT_AMOUNT_NBT);
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


    public class CompressorItemOutputHandler implements IItemHandlerModifiable {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            return productStack.copyWithCount(EverythingMathUtil.overflowingLongToInt(product));
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean simulate) {
            return itemStack;
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean simulate) {
            int count = Math.min(i1, EverythingMathUtil.overflowingLongToInt(product));
            if (!simulate) {
                product -= count;
            }
            return productStack.copyWithCount(count);
        }

        @Override
        public int getSlotLimit(int i) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            return false;
        }

        @Override
        public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
            if (ItemStack.isSameItemSameTags(getProductStack(),itemStack.copyWithCount(1))) {
                EverythingCompressorBlockEntity.this.product += ((long) itemStack.getCount() - product);
            } else {
                EverythingCompressorBlockEntity.this.product = itemStack.getCount();
            }
            EverythingCompressorBlockEntity.this.setProductStack(itemStack.copyWithCount(1));
        }
    }

    public class CompressorSlotItemOutputHandler implements IItemHandlerModifiable {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            int count = Math.min(getSlotLimit(i), EverythingMathUtil.overflowingLongToInt(product));
            return getProductStack().copyWithCount(count);
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) {
            return itemStack;
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean simulate) {
            int count = Math.min(i1, EverythingMathUtil.overflowingLongToInt(product));
            if (!simulate) {
                product -= count;
            }
            return productStack.copyWithCount(count);
        }

        @Override
        public int getSlotLimit(int i) {
            return productStack.getMaxStackSize();
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            return false;
        }

        @Override
        public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
            int delta = itemStack.getCount() - this.getStackInSlot(i).getCount();
            product = EverythingMathUtil.overflowingAdd(product, delta);
            if (!ItemStack.isSameItemSameTags(productStack, itemStack) && level.isClientSide()) {
                productStack = itemStack.copyWithCount(1);
            }
        }
    }
}
