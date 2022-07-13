/*
 * This file is part of Ability and is licensed to the project under
 * terms that are compatible with the GNU Lesser General Public License.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership and licensing.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package grondag.ab.building.block;

import static net.minecraft.world.level.block.StairBlock.WATERLOGGED;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import grondag.ab.Ability;
import grondag.ab.building.block.base.FormedBlock;
import grondag.ab.building.block.base.FormedBlockEntity;
import grondag.ab.building.block.base.FormedBlockType;
import grondag.ab.building.block.base.ShapeType;
import grondag.ab.building.block.init.FormedBlocks;
import grondag.xm.api.block.XmBlockState;
import grondag.xm.api.collision.CollisionDispatcher;
import grondag.xm.api.collision.CollisionShapes;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public class BasicBlock extends Block implements EntityBlock, FormedBlock {
	// WIP: should move these to own place
	public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("ab_light", 0, 15);

	public static final PrimitiveStateMutator SIMPLE_SPECIES_MUTATOR = PrimitiveStateMutator.builder()
			.withJoin(SpeciesProperty.matchBlockAndSpecies())
			.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
			.build();

	private final @Nullable SpeciesFunction speciesFunc;

	protected final FormedBlockType formedBlockType;
	protected final boolean isNotFullCube;
	protected final boolean useShapeForOcclusion;

	public BasicBlock(FormedBlockType formedBlockType) {
		super(setThreadLocalIdentityAndCreateSettings(formedBlockType));
		this.formedBlockType = formedBlockType;
		assert formedBlockType == INIT_BLOCK_TYPE.get();

		isNotFullCube = formedBlockType.shape.shapeType != ShapeType.CUBE;
		useShapeForOcclusion = !formedBlockType.shape.shapeType.isFullOccluder;
		speciesFunc = formedBlockType.shape.useSpecies ?  SpeciesProperty.speciesForBlock(this) : null;

		if (isNotFullCube) {
			registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
		}
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos pos, CollisionContext entityContext) {
		switch(formedBlockType.shape.shapeType) {
			case DYNAMIC_CUBE_WITH_CUTOUTS:
			case DYNAMIC_NON_CUBIC:
			case STATIC_NON_CUBIC:
				return CollisionDispatcher.shapeFor(XmBlockState.modelState(blockState, blockView, pos, true));
			case OCCLUDING_CUBE_WITH_CUTOUTS:
				return CollisionShapes.CUBE_WITH_CUTOUTS;
			case CUBE:
			default:
				return Shapes.block();
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(LIGHT_LEVEL);

		// Note that we can't use class members here because this is called from super constructor before they can be set
		final var blockType = INIT_BLOCK_TYPE.get();

		if (blockType.shape.shapeType != ShapeType.CUBE) {
			builder.add(WATERLOGGED);
		}

		if (blockType.shape.useSpecies) {
			builder.add(SpeciesProperty.SPECIES);
		}
	}

	@Deprecated
	@Override
	public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
		if (isNotFullCube && blockState.getValue(WATERLOGGED).booleanValue()) {
			levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
		}

		return super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
	}

	@Deprecated
	@Override
	public FluidState getFluidState(BlockState blockState) {
		return isNotFullCube && blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
	}

	@Deprecated
	@Override
	public boolean useShapeForLightOcclusion(BlockState blockState_1) {
		return useShapeForOcclusion;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return FormedBlocks.formedBlockEntity(pos, state);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		var result = super.getStateForPlacement(blockPlaceContext);

		if (isNotFullCube) {
			final FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
			result = result.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
		}

		if (speciesFunc != null) {
			final var mode = Ability.forceKey.isPressed(blockPlaceContext.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
			final int species = Species.speciesForPlacement(blockPlaceContext, mode, speciesFunc);
			result = result.setValue(SpeciesProperty.SPECIES, species);
		}

		return result;
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos blockPos, BlockState blockState, Player playerEntity) {
		// Drop in creative mode
		if (!playerEntity.isCreative() && !world.isClientSide) {
			final BlockEntity blockEntity = world.getBlockEntity(blockPos);

			if (blockEntity instanceof FormedBlockEntity) {
				final ItemStack itemStack = new ItemStack(this);
				blockEntity.saveToItem(itemStack);
				final ItemEntity itemEntity = new ItemEntity(world, blockPos.getX() + 0.5D, blockPos.getY() + 0.5D, blockPos.getZ() + 0.5D, itemStack);
				itemEntity.setDefaultPickUpDelay();
				world.addFreshEntity(itemEntity);
			}
		}

		super.playerWillDestroy(world, blockPos, blockState, playerEntity);
	}

	@Override
	public FormedBlockType formedBlockType() {
		return formedBlockType;
	}

	/**
	 * We set this shape as a threadlocal in intialization
	 * initialization because we need it to be available in a routine called from
	 * super constructor and thus it won't be set in class members.
	 */
	private static Properties setThreadLocalIdentityAndCreateSettings(FormedBlockType blockType) {
		INIT_BLOCK_TYPE.set(blockType);
		return blockType.createSettings();
	}

	private static final ThreadLocal<FormedBlockType> INIT_BLOCK_TYPE = new ThreadLocal<>();
}
