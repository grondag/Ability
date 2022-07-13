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

package grondag.ab.building.block.base;

import java.util.function.Consumer;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.level.block.Block;

import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public class FormedBlockShape {
	public final String code;
	public final Function<FormedBlockMaterial, PrimitiveState> defaultModelStateFunc;
	public final PrimitiveStateMutator stateFunc;
	private final Function<FormedBlockType, Block> factory;
	public final ShapeType shapeType;
	public final boolean useSpecies;

	public FormedBlockShape (
			String code,
			Function<FormedBlockMaterial, PrimitiveState> defaultModelStateFunc,
			PrimitiveStateMutator stateFunc,
			Function<FormedBlockType, Block> factory,
			ShapeType shapeType,
			boolean useSpecies
	) {
		this.code = code;
		this.defaultModelStateFunc = defaultModelStateFunc;
		this.stateFunc = stateFunc;
		this.factory = factory;
		this.shapeType = shapeType;
		this.useSpecies = useSpecies;
		ALL.add(this);
	}

	public Block createBlock(FormedBlockType blockType) {
		assert blockType.shape == this;
		return factory.apply(blockType);
	}

	private static final ObjectArrayList<FormedBlockShape> ALL = new ObjectArrayList<>();

	public static void forEach(Consumer<FormedBlockShape> consumer) {
		ALL.forEach(consumer);
	}
}
