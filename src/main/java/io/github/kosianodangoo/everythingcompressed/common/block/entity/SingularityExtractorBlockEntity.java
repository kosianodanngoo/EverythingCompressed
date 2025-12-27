package io.github.kosianodangoo.everythingcompressed.common.block.entity;

import io.github.kosianodangoo.everythingcompressed.EverythingCompressedConfig;
import io.github.kosianodangoo.everythingcompressed.common.api.ICompressionInfo;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import io.github.kosianodangoo.everythingcompressed.common.menu.EverythingCompressorMenu;
import io.github.kosianodangoo.everythingcompressed.common.menu.SingularityExtractorMenu;
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
import net.minecraft.world.item.Item;
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

public class SingularityExtractorBlockEntity extends BlockEntity implements MenuProvider {
    public static final String PROGRESS_NBT = "progress";
    public static final String PRODUCT_AMOUNT_NBT = "productAmount";
    public static final String PRODUCT_NBT = "product";
    public static final String STORED_AMOUNT_NBT = "storedAmount";
    public static final String STORED_NBT = "stored";

    public final ContainerData data;

    private static ForgeConfigSpec.LongValue EXTRACTION_TIME = EverythingCompressedConfig.EXTRACTION_TIME;

    public ExtractorItemInputHandler inputHandler = new ExtractorItemInputHandler();
    public ExtractorItemOutputHandler outputHandler = new ExtractorItemOutputHandler();
    public IItemHandler slotOutputHandler = new ExtractorSlotItemOutputHandler();
    public CombinedInvWrapper combinedInv = new CombinedInvWrapper(inputHandler, outputHandler);

    public LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> combinedInv);

    private long stored = 0;
    private long product = 0;
    private long progress = 0;

    private ItemStack storedStack = ItemStack.EMPTY;
    private ItemStack productStack = ItemStack.EMPTY;

    public SingularityExtractorBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> (int) (progress & (long) -1);
                    case 1 -> (int) (progress >> Integer.SIZE & (long) -1);
                    case 2 -> (int) (getStored() & (long) -1);
                    case 3 -> (int) (getStored() >> Integer.SIZE & (long) -1);
                    case 4 -> (int) (product & (long) -1);
                    case 5 -> (int) (product >> Integer.SIZE & (long) -1);
                    case 6 -> (int) (getExtractionTime() & (long) -1);
                    case 7 -> (int) (getExtractionTime() >> Integer.SIZE & (long) -1);
                    default -> 0;
                };
            }
            @Override
            public void set(int i, int i1) {
            }
            @Override
            public int getCount() {
                return 8;
            }
        };
    }

    public SingularityExtractorBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        this(ModBlockEntityTypes.SINGULARITY_EXTRACTOR.get(), p_155229_, p_155230_);
    }

    @Override
    public Component getDisplayName() {
        return this.getBlockState().getBlock().getName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new SingularityExtractorMenu(i, inventory, this, this.data);
    }


    public IItemHandlerModifiable getInventory() {
        return this.combinedInv;
    }

    public long getProgress() {
        return progress;
    }

    public long getStored() {
        return stored;
    }

    public ItemStack getStoredStack() {
        return storedStack;
    }

    public void setStoredStack(ItemStack stack) {
        this.storedStack = stack;
    }

    public long getProduct() {
        return product;
    }

    public void setProduct(long amount) {
        this.product = amount;
    }

    public ItemStack getProductStack() {
        return productStack;
    }

    public void setProductStack(ItemStack stack) {
        this.productStack = stack;
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        processExtracting();
        if (progress <= 0 && product <= 0) {
            setStoredStack(ItemStack.EMPTY);
            setProductStack(ItemStack.EMPTY);
        }
    }

    public void processExtracting() {
        if (getStoredStack().isEmpty()) {
            return;
        }
        progress = EverythingMathUtil.overflowingAdd(progress, stored);
        if (getProductStack().isEmpty()) {
            setProductStack(CompressionInfoUtil.getCompressedStack(getStoredStack()).orElse(ItemStack.EMPTY));
        }
        long extractionTime = getExtractionTime();
        if (progress >= extractionTime) {
            long production = progress / extractionTime;
            product = EverythingMathUtil.overflowingAdd(product, production);
            progress -= production * extractionTime;
        }
    }

    public long getExtractionTime() {
        return EXTRACTION_TIME.get();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    public void addStack(ItemStack stack, long count) {
        if (!isValidStack(stack)) {
            return;
        }
        if (getStoredStack().isEmpty()) {
            setStoredStack(stack.copyWithCount(1));
        }
        stored = EverythingMathUtil.overflowingAdd(stored, count);
    }

    public void addStack(ItemStack stack) {
        addStack(stack, stack.getCount());
    }

    public boolean isValidStack(ItemStack stack) {
        ICompressionInfo compressionInfo = CompressionInfoUtil.getCompressionInfo(stack);
        return (compressionInfo != null && getStoredStack().isEmpty())  || (getStored() < Long.MAX_VALUE && ItemStack.isSameItemSameTags(getStoredStack(), stack));
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        compoundTag.putLong(PROGRESS_NBT, getProgress());
        compoundTag.put(PRODUCT_NBT, getProductStack().serializeNBT());
        compoundTag.putLong(PRODUCT_AMOUNT_NBT, product);
        compoundTag.put(STORED_NBT, getStoredStack().serializeNBT());
        compoundTag.putLong(STORED_AMOUNT_NBT, getStored());
    }

    @Override
    public void load(@NotNull CompoundTag compoundTag) {
        super.load(compoundTag);
        this.progress = compoundTag.getLong(PROGRESS_NBT);
        this.setProductStack(ItemStack.of(compoundTag.getCompound(PRODUCT_NBT)));
        this.product = compoundTag.getLong(PRODUCT_AMOUNT_NBT);
        this.setStoredStack(ItemStack.of(compoundTag.getCompound(STORED_NBT)));
        this.stored = compoundTag.getLong(STORED_AMOUNT_NBT);
    }

    public class ExtractorItemInputHandler implements IItemHandlerModifiable {
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
            SingularityExtractorBlockEntity.this.addStack(itemStack);
        }
    }

    public class ExtractorItemOutputHandler implements IItemHandlerModifiable {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            return productStack.copyWithCount((int) product);
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean simulate) {
            return itemStack;
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean simulate) {
            int count = Math.min(i1, (int) product);
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
                SingularityExtractorBlockEntity.this.product += ((long) itemStack.getCount() - product);
            } else {
                SingularityExtractorBlockEntity.this.product = itemStack.getCount();
            }
            SingularityExtractorBlockEntity.this.setProductStack(itemStack.copyWithCount(1));
        }
    }

    public class ExtractorSlotItemOutputHandler implements IItemHandlerModifiable {
        ItemStack toDisplay = ItemStack.EMPTY;

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int i) {
            int count = Math.min(getSlotLimit(i), (int) product);
            return productStack.copyWithCount(count);
        }

        @Override
        public @NotNull ItemStack insertItem(int i, @NotNull ItemStack itemStack, boolean b) {
            return itemStack;
        }

        @Override
        public @NotNull ItemStack extractItem(int i, int i1, boolean simulate) {
            int count = Math.min(i1, (int) product);
            if (!simulate) {
                product -= count;
            }
            return productStack.copyWithCount(count);
        }

        @Override
        public int getSlotLimit(int i) {
            return 64;
        }

        @Override
        public boolean isItemValid(int i, @NotNull ItemStack itemStack) {
            return false;
        }

        @Override
        public void setStackInSlot(int i, @NotNull ItemStack itemStack) {
            int delta = itemStack.getCount() - this.getStackInSlot(i).getCount();
            product = EverythingMathUtil.overflowingAdd(product, delta);
            if (!ItemStack.isSameItemSameTags(productStack, itemStack)) {
                productStack = itemStack.copyWithCount(1);
                product = itemStack.getCount();
            }
        }
    }
}
