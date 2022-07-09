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

import org.jetbrains.annotations.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

import grondag.ab.building.gui.PaintScreen;
import grondag.xm.api.modelstate.ModelState;
import grondag.xm.api.modelstate.MutableModelState;
import grondag.xm.api.modelstate.primitive.MutablePrimitiveState;
import grondag.xm.api.paint.PaintIndex;
import grondag.xm.modelstate.AbstractPrimitiveModelState;

public class BlockPlacementTool extends Item {
	public BlockPlacementTool(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResult useOn(UseOnContext useOnContext) {
		final var placementResult = place(new BlockPlaceContext(useOnContext));

		if (!placementResult.consumesAction()) {
			final var secondaryResult = use(useOnContext.getLevel(), useOnContext.getPlayer(), useOnContext.getHand()).getResult();
			return secondaryResult == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : secondaryResult;
		} else {
			return placementResult;
		}
	}

	public InteractionResult place(BlockPlaceContext placementContext) {
		if (!placementContext.canPlace()) {
			return InteractionResult.FAIL;
		} else {
			final var blockState = getPlacementState(placementContext);

			if (blockState == null) {
				return InteractionResult.FAIL;
			} else if (!placementContext.getLevel().setBlock(placementContext.getClickedPos(), blockState, 11)) {
				return InteractionResult.FAIL;
			} else {
				final var blockPos = placementContext.getClickedPos();
				final var level = placementContext.getLevel();
				final var player = placementContext.getPlayer();
				final var itemStack = placementContext.getItemInHand();
				final var worldBlockState = level.getBlockState(blockPos);

				if (worldBlockState.is(blockState.getBlock())) {
					BlockItem.updateCustomBlockEntityTag(level, player, blockPos, itemStack);
					worldBlockState.getBlock().setPlacedBy(level, blockPos, worldBlockState, player, itemStack);

					if (player instanceof ServerPlayer) {
						CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockPos, itemStack);
					}
				}

				final SoundType soundType = worldBlockState.getSoundType();
				level.playSound(player, blockPos, worldBlockState.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
				level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, worldBlockState));

				return InteractionResult.sidedSuccess(level.isClientSide);
			}
		}
	}

	@Nullable
	protected BlockState getPlacementState(BlockPlaceContext blockPlaceContext) {
		final var blockState = getBlock().getStateForPlacement(blockPlaceContext);
		return blockState != null && canPlace(blockPlaceContext, blockState) ? blockState : null;
	}

	protected boolean canPlace(BlockPlaceContext blockPlaceContext, BlockState blockState) {
		final var player = blockPlaceContext.getPlayer();
		final var collisionContext = player == null ? CollisionContext.empty() : CollisionContext.of(player);
		return (blockState.canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos()))
			&& blockPlaceContext.getLevel().isUnobstructed(blockState, blockPlaceContext.getClickedPos(), collisionContext);
	}

	protected Block getBlock() {
		return Building.DEFAULT_ABILITY_BLOCK;
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

		final CompoundTag tag = BlockItem.getBlockEntityData(stack);

		if (tag != null && tag.contains(FormedBlockEntity.TAG_MODEL_STATE)) {
			return (MutablePrimitiveState) ModelState.fromTag(tag.getCompound(FormedBlockEntity.TAG_MODEL_STATE), PaintIndex.forWorld(world));
		} else {
			return ((BlockModelStateProvider) getBlock()).defaultModelState().mutableCopy();
		}
	}

	public void writeModelState(ItemStack stack, MutablePrimitiveState modelState) {
		assert stack.getItem() == this;
		var tag = BlockItem.getBlockEntityData(stack);

		if (tag == null) {
			tag = new CompoundTag();
			tag.put(FormedBlockEntity.TAG_MODEL_STATE, modelState.toTag());
			BlockItem.setBlockEntityData(stack, Building.formedBlockEntityType, tag);
		} else {
			tag.put(FormedBlockEntity.TAG_MODEL_STATE, modelState.toTag());
		}
	}

	public static final BiFunction<ItemStack, Level, MutableModelState> ITEM_MODEL_FUNCTION  = (s, w) -> {
		if (s.getItem() instanceof BlockPlacementTool) {
			return ((BlockPlacementTool) s.getItem()).readModelState(s, w);
		}

		return null;
	};
}
