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

package grondag.ab.building.block.init;

import java.util.function.Consumer;
import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.level.block.Block;

import grondag.ab.building.block.StairLike;
import grondag.ab.building.block.SimpleCube;
import grondag.ab.building.block.SquareInsetColumn;
import grondag.xm.api.primitive.simple.Stair;
import grondag.xm.api.primitive.simple.Wedge;
import grondag.xm.orientation.api.CubeRotation;

public class FormedBlockShape {
	public final String code;
	private final Function<FormedBlockMaterial, Block> factory;

	private FormedBlockShape (
			String code,
			Function<FormedBlockMaterial, Block> factory
	) {
		this.code = code;
		this.factory = factory;
		ALL.add(this);
	}

	public Block createBlock(FormedBlockMaterial material) {
		return factory.apply(material);
	}

	private static final ObjectArrayList<FormedBlockShape> ALL = new ObjectArrayList<>();

	public static void forEach(Consumer<FormedBlockShape> consumer) {
		ALL.forEach(consumer);
	}

	public static final FormedBlockShape CUBE = new FormedBlockShape("cube-u", SimpleCube::create);

	public static final FormedBlockShape WEDGE = new FormedBlockShape("wedge-s", m -> StairLike.create(m, Wedge.INSTANCE, StairLike.Shape.STRAIGHT, CubeRotation.DOWN_WEST));
	public static final FormedBlockShape INSIDE_WEDGE = new FormedBlockShape("wedge-i", m -> StairLike.create(m, Wedge.INSTANCE, StairLike.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH));
	public static final FormedBlockShape OUTSIDE_WEDGE = new FormedBlockShape("wedge-o", m -> StairLike.create(m, Wedge.INSTANCE, StairLike.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH));

	public static final FormedBlockShape STAIR = new FormedBlockShape("stair-s", m -> StairLike.create(m, Stair.INSTANCE, StairLike.Shape.STRAIGHT, CubeRotation.DOWN_WEST));
	public static final FormedBlockShape INSIDE_STAIR = new FormedBlockShape("stair-i", m -> StairLike.create(m, Stair.INSTANCE, StairLike.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH));
	public static final FormedBlockShape OUTSIDE_STAIR = new FormedBlockShape("stair-o", m -> StairLike.create(m, Stair.INSTANCE, StairLike.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH));

	public static final FormedBlockShape SQUARE_INSET_COLUMN = new FormedBlockShape("sqcol-i", SquareInsetColumn::create);
}
