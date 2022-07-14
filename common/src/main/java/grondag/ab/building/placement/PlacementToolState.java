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

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import grondag.ab.building.block.base.FormedBlockType;
import grondag.ab.building.block.init.FormedBlocks;
import grondag.ab.building.gui.LayerSelector;
import grondag.xm.api.modelstate.primitive.PrimitiveState;

public class PlacementToolState {
	private ItemStack stack;
	private InteractionHand hand;
	private FormedBlockType blockType;
	private PrimitiveState modelState;

	protected int selectedLayer =  0;

	protected LayerSelector[] layers = new LayerSelector[3];

	public void load(ItemStack stack, InteractionHand hand, Level level) {
		assert stack.getItem() == FormedBlocks.BLOCK_PLACEMENT_TOOL;
		this.stack = stack;
		this.hand = hand;
		this.modelState = BlockPlacementTool.readModelState(stack, level);
		this.blockType = BlockPlacementTool.getBlockType(stack);
	}

	public PrimitiveState modelState() {
		return modelState;
	}

	public void modelState(PrimitiveState modelState) {
		this.modelState = modelState.toImmutable();
		BlockPlacementTool.writeModelState(stack, this.modelState);
	}

	public FormedBlockType blockType() {
		return blockType;
	}

	public void blockType(FormedBlockType blockType) {
		this.blockType = blockType;
		BlockPlacementTool.setBlockType(stack, blockType);
		this.modelState = blockType.defaultModelState;
		BlockPlacementTool.writeModelState(stack, this.modelState);
	}

	public ItemStack displayStack() {
		return stack;
	}

	public InteractionHand hand() {
		return hand;
	}

}
