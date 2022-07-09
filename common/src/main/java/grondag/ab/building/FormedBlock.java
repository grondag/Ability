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

package grondag.ab.building;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;

import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public class FormedBlock extends Block implements EntityBlock, BlockModelStateProvider {
	protected final BlockEntitySupplier<? extends BlockEntity> beFactory;
	protected final PrimitiveState defaultModelState;
	protected final PrimitiveStateMutator stateFunc;

	protected FormedBlock(Properties settings, BlockEntitySupplier<? extends BlockEntity> beFactory, PrimitiveState defaultModelState, PrimitiveStateMutator stateFunc) {
		super(settings);
		this.beFactory = beFactory;
		this.defaultModelState = defaultModelState.toImmutable();
		this.stateFunc = stateFunc;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return beFactory.create(pos, state);
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
	public PrimitiveState defaultModelState() {
		return defaultModelState;
	}

	@Override
	public PrimitiveStateMutator stateFunction() {
		return stateFunc;
	}
}
