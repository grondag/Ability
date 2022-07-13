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

import java.util.IdentityHashMap;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import grondag.ab.building.block.BasicBlock;
import grondag.xm.api.modelstate.primitive.PrimitiveState;

public class FormedBlockType {
	public final FormedBlockMaterial material;
	public final FormedBlockShape shape;
	public final String name;
	public final PrimitiveState defaultModelState;

	public FormedBlockType (FormedBlockMaterial material, FormedBlockShape shape) {
		this.material = material;
		this.shape = shape;
		name = material.code() + "-" + shape.code;
		this.defaultModelState = shape.defaultModelStateFunc.apply(material);
		addToMap(this);
	}

	public Properties createSettings() {
		final var result = material.settings()
				.lightLevel(b -> b.getOptionalValue(BasicBlock.LIGHT_LEVEL).orElse(0));

		return shape.shapeType.setup.apply(result);
	}

	public static FormedBlockType of(FormedBlockMaterial material, FormedBlockShape shape) {
		return new FormedBlockType(material, shape);
	}

	private static IdentityHashMap<FormedBlockMaterial, IdentityHashMap<FormedBlockShape, FormedBlockType>> MAP = new IdentityHashMap<>();

	private static void addToMap(FormedBlockType blockType) {
		var innerMap = MAP.get(blockType.material);

		if (innerMap == null ) {
			innerMap = new IdentityHashMap<>();
			MAP.put(blockType.material, innerMap);
		}

		innerMap.put(blockType.shape, blockType);
	}

	public static FormedBlockType get(FormedBlockMaterial material, FormedBlockShape shape) {
		final var innerMap = MAP.get(material);

		return innerMap == null ? null : innerMap.get(shape);
	}
}
