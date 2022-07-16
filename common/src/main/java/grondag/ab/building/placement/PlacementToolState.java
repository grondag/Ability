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

package grondag.ab.building.placement;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import grondag.ab.building.block.base.FormedBlockEntity;
import grondag.ab.building.block.base.FormedBlockType;
import grondag.ab.building.block.init.FormedBlocks;
import grondag.xm.api.modelstate.ModelState;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.paint.PaintIndex;

public class PlacementToolState {
	private ItemStack stack;
	private InteractionHand hand;
	private FormedBlockType blockType;
	private PrimitiveState modelState;
	private final PaintState paintState = new PaintState();

	//protected int selectedLayer =  0;

	//protected LayerSelector[] layers = new LayerSelector[3];

	public PrimitiveState modelState() {
		return modelState;
	}

	@Deprecated
	public void modelState(PrimitiveState modelState) {
		this.modelState = modelState.toImmutable();
		PlacementToolState.setModelState(stack, this.modelState);
	}

	public FormedBlockType blockType() {
		return blockType;
	}

	public void blockType(FormedBlockType blockType) {
		this.blockType = blockType;
		this.paintState.switchMaterial(blockType.material);
		PlacementToolState.setBlockType(stack, blockType);
		this.modelState = blockType.defaultModelState.mutableCopy().apply(paintState::applyToState).releaseToImmutable();
		PlacementToolState.setModelState(stack, this.modelState);
	}

	public ItemStack displayStack() {
		return stack;
	}

	public InteractionHand hand() {
		return hand;
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeInt(blockType.index);
		buf.writeBoolean(hand == InteractionHand.OFF_HAND);
		modelState().toBytes(buf);
		paintState.toBytes(buf);
	}

	public void fromBytes(FriendlyByteBuf buf, Level level) {
		blockType = FormedBlockType.get(buf.readInt());
		hand = buf.readBoolean() ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
		modelState = (PrimitiveState) ModelState.fromBytes(buf, PaintIndex.forWorld(level)).toImmutable();
		paintState.fromBytes(buf, level);
	}

	public void toItem(ItemStack stack) {
		setModelState(stack, modelState);
		setBlockType(stack, blockType);
		stack.addTagElement(PAINT_STATE_TAG, paintState.toTag());
	}

	public void fromItem(ItemStack stack, InteractionHand hand, Level level) {
		assert stack.getItem() == FormedBlocks.BLOCK_PLACEMENT_TOOL;
		this.stack = stack;
		this.hand = hand;
		this.modelState = PlacementToolState.getModelState(stack, level);
		this.blockType = PlacementToolState.getBlockType(stack);
		this.paintState.fromTag(stack.getTagElement(PAINT_STATE_TAG), level);
	}

	public static final String PAINT_STATE_TAG = "ab_ps";
	private static final String BLOCK_TYPE_TAG = "ab_bt";

	private static void setModelState(ItemStack stack, ModelState modelState) {
		var tag = BlockItem.getBlockEntityData(stack);

		if (tag == null) {
			tag = new CompoundTag();
			tag.put(FormedBlockEntity.TAG_MODEL_STATE, modelState.toTag());
			BlockItem.setBlockEntityData(stack, FormedBlocks.formedBlockEntityType, tag);
		} else {
			tag.put(FormedBlockEntity.TAG_MODEL_STATE, modelState.toTag());
		}
	}

	public static MutablePrimitiveState getModelState(ItemStack stack, Level world) {
		final CompoundTag tag = BlockItem.getBlockEntityData(stack);

		if (tag != null && tag.contains(FormedBlockEntity.TAG_MODEL_STATE)) {
			return (MutablePrimitiveState) ModelState.fromTag(tag.getCompound(FormedBlockEntity.TAG_MODEL_STATE), PaintIndex.forWorld(world));
		} else {
			return getBlockType(stack).defaultModelState.mutableCopy();
		}
	}

	private static void setBlockType(ItemStack stack, FormedBlockType blockType) {
		if (blockType == null) {
			blockType = FormedBlocks.DEFAULT_ABILITY_BLOCK_TYPE;
		}

		stack.getOrCreateTag().putString(PlacementToolState.BLOCK_TYPE_TAG, blockType.name);
	}

	public static FormedBlockType getBlockType(ItemStack stack) {
		final var tag = stack.getTag();

		if (tag != null && tag.contains(PlacementToolState.BLOCK_TYPE_TAG)) {
			final var blockType = FormedBlockType.get(tag.getString(PlacementToolState.BLOCK_TYPE_TAG));
			return blockType == null ? FormedBlocks.DEFAULT_ABILITY_BLOCK_TYPE : blockType;
		} else {
			return FormedBlocks.DEFAULT_ABILITY_BLOCK_TYPE;
		}
	}
}
