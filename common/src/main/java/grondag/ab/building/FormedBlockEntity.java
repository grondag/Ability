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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import grondag.ab.varia.SafeBlockRenderUpdate;
import grondag.xm.api.modelstate.ModelState;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateFunction;
import grondag.xm.api.paint.PaintIndex;

public class FormedBlockEntity extends BlockEntity {
	protected MutablePrimitiveState modelState;
	public static final String TAG_MODEL_STATE = ("abms");

	public FormedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
	}

	// PERF: cache world refresh
	public MutablePrimitiveState getModelState(boolean refreshFromWorld) {
		final var block = (BlockModelStateProvider) this.getBlockState().getBlock();

		MutablePrimitiveState result = modelState;

		if (result == null) {
			modelState = block.defaultModelState().mutableCopy();
			result = modelState;
			refreshFromWorld = true;
		}

		if (refreshFromWorld && !result.isStatic()) {
			block.stateFunction().mutate(result, getBlockState(), level, worldPosition, null, refreshFromWorld);
		}

		return result.mutableCopy();
	}


	public void setModelStateState(PrimitiveState newState) {
		// PERF: can copy instead of release?
		if (modelState != null) {
			modelState.release();
		}

		modelState = newState.mutableCopy();

		markForSave();
	}

	@Override
	protected void saveAdditional(CompoundTag compoundTag) {
		super.saveAdditional(compoundTag);

		if (modelState != null) {
			compoundTag.put(TAG_MODEL_STATE, modelState.toTag());
		} else {
			compoundTag.put(TAG_MODEL_STATE, ((BlockModelStateProvider) getBlockState().getBlock()).defaultModelState().toTag());
		}
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);

		if (tag.contains(TAG_MODEL_STATE)) {
			final var newModelState = (MutablePrimitiveState) ModelState.fromTag(tag.getCompound(TAG_MODEL_STATE), PaintIndex.forWorld(level));

			// PERF can copy instead of allocate?
			if (modelState != null) {
				// Skip check when is null because that is initial load and render refresh will happen anyway
				if (level.isClientSide() && !modelState.equals(newModelState)) {
					SafeBlockRenderUpdate.PROXY.updateBlockRender(worldPosition);
				}

				modelState.release();
			}

			modelState = newModelState;
		}
	}

	protected void markForSave() {
		if (level != null && worldPosition != null) {
			level.blockEntityChanged(worldPosition);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public static final PrimitiveStateFunction STATE_ACCESS_FUNC = (state, world, pos, refresh) -> {
		if (world != null && pos != null) {
			final BlockEntity be = world.getBlockEntity(pos);

			if (be != null) {
				return ((FormedBlockEntity) world.getBlockEntity(pos)).getModelState(refresh);
			}
		}

		return ((BlockModelStateProvider) state.getBlock()).defaultModelState().mutableCopy();
	};
}
