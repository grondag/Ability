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

import grondag.ab.building.gui.LayerSelector;
import grondag.xm.api.item.XmItem;
import grondag.xm.api.modelstate.primitive.PrimitiveState;

public class PlacementToolState {
	protected ItemStack stack;
	protected InteractionHand hand;

	protected int selectedLayer =  0;

	protected LayerSelector[] layers = new LayerSelector[3];

	PrimitiveState modelState;

	public void load(ItemStack stack, InteractionHand hand, Level level) {
		this.stack = stack;
		this.hand = hand;
		this.modelState = XmItem.modelState(level, stack);
	}

	public void modelState(PrimitiveState modelState) {
		this.modelState = modelState.toImmutable();
	}

	public PrimitiveState modelState() {
		return modelState;
	}

	public ItemStack displayStack() {
		return stack;
	}

	public InteractionHand hand() {
		return hand;
	}
}
