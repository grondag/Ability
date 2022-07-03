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

import java.util.function.BiFunction;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import grondag.ab.building.gui.PaintScreen;
import grondag.xm.api.modelstate.ModelState;
import grondag.xm.api.modelstate.MutableModelState;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.paint.PaintIndex;
import grondag.xm.modelstate.AbstractPrimitiveModelState;

public class FormedBlockItem extends BlockItem {
	public FormedBlockItem(FormedBlock block, Properties settings) {
		super(block, settings);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		//		if(!ctx.getPlayer().isSneaking()) {
		//			if(use(ctx.getWorld(), ctx.getPlayer(), ctx.getHand()).getResult().isAccepted()) {
		//				return ActionResult.SUCCESS;
		//			}
		//		}

		return super.useOn(ctx);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
		final ItemStack itemStack = playerEntity.getItemInHand(hand);

		if (world.isClientSide) {
			Minecraft.getInstance().setScreen(new PaintScreen(itemStack, hand));
		}

		return InteractionResultHolder.success(itemStack);
	}

	public void acceptClientModelStateUpdate(Player player, ItemStack itemStack, ModelState modelState, boolean offHand) {
		final MutablePrimitiveState stackState = readModelState(itemStack, player.level);

		if (!modelState.isStatic() && stackState.primitive()  == ((AbstractPrimitiveModelState<?, ?, ?>) modelState).primitive()) {
			stackState.copyFrom(modelState);
			writeModelState(itemStack, stackState);
			player.setItemInHand(offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, itemStack);
		}

		stackState.release();
	}

	public MutablePrimitiveState readModelState(ItemStack stack, Level world) {
		assert stack.getItem() == this;

		final CompoundTag tag = getBlockEntityData(stack);

		if (tag != null && tag.contains(FormedBlockEntity.TAG_MODEL_STATE)) {
			return (MutablePrimitiveState) ModelState.fromTag(tag.getCompound(FormedBlockEntity.TAG_MODEL_STATE), PaintIndex.forWorld(world));
		} else {
			return ((FormedBlock) getBlock()).defaultModelState.mutableCopy();
		}
	}

	public void writeModelState(ItemStack stack, MutablePrimitiveState modelState) {
		assert stack.getItem() == this;
		BlockItem.getBlockEntityData(stack).put(FormedBlockEntity.TAG_MODEL_STATE, modelState.toTag());
	}

//	@Override
//	protected boolean postPlacement(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
//		if (world.isClient) {
//			final CompoundTag compoundTag = stack.getSubTag("BlockEntityTag");
//
//			if (compoundTag != null) {
//				final BlockEntity blockEntity = world.getBlockEntity(pos);
//
//				if (blockEntity != null && blockEntity instanceof final HsBlockEntity be) {
//					be.setModelStateState(readModelState(stack, world));
//				}
//			}
//
//			return false;
//		} else {
//			return writeTagToBlockEntity(world, player, pos, stack);
//		}
//	}

	public static final BiFunction<ItemStack, Level, MutableModelState> FORMED_BLOCK_ITEM_MODEL_FUNCTION  = (s, w) -> {
		if (s.getItem() instanceof FormedBlockItem) {
			return ((FormedBlockItem) s.getItem()).readModelState(s, w);
		}

		return null;
	};
}
