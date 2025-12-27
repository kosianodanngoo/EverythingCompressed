package io.github.kosianodangoo.everythingcompressed.common.block;

import io.github.kosianodangoo.everythingcompressed.common.block.entity.EverythingCompressorBlockEntity;
import io.github.kosianodangoo.everythingcompressed.common.block.entity.SingularityExtractorBlockEntity;
import io.github.kosianodangoo.everythingcompressed.common.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingularityExtractorBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public SingularityExtractorBlock()  {
        this(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(2.0F, 3.0F));
    }

    public SingularityExtractorBlock(BlockBehaviour.Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new SingularityExtractorBlockEntity(blockPos, blockState);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide() || !pBlockEntityType.equals(ModBlockEntityTypes.SINGULARITY_EXTRACTOR.get())){
            return null;
        }
        return (level, pos, state, tile) -> ((SingularityExtractorBlockEntity) tile).tick(level, pos, state);
    }
}
