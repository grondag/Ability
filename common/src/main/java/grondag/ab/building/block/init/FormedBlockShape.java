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

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import grondag.ab.building.block.PanelFlat;
import grondag.ab.building.block.PanelInset;
import grondag.ab.building.block.RoundColumn;
import grondag.ab.building.block.RoundColumnCut;
import grondag.ab.building.block.RoundColumnRoundCap;
import grondag.ab.building.block.RoundColumnSquareCap;
import grondag.ab.building.block.SimpleCube;
import grondag.ab.building.block.SquareColumnCapped;
import grondag.ab.building.block.SquareColumnGrooved;
import grondag.ab.building.block.StairLike;
import grondag.ab.building.block.base.FormedNonCubicFacingBlock;
import grondag.xm.api.primitive.simple.Slab;
import grondag.xm.api.primitive.simple.Stair;
import grondag.xm.api.primitive.simple.Wedge;
import grondag.xm.api.primitive.simple.WedgeCap;
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

	public static final FormedBlockShape CUBE = new FormedBlockShape("cube", SimpleCube::create);

	public static final FormedBlockShape PANEL_INSET = new FormedBlockShape("pnl-ins", PanelInset::create);
	public static final FormedBlockShape PANEL_FLAT = new FormedBlockShape("pnl-flt", PanelFlat::create);

	public static final FormedBlockShape WEDGE = new FormedBlockShape("wedge", m -> StairLike.create(m, Wedge.INSTANCE, StairLike.Shape.STRAIGHT, CubeRotation.DOWN_WEST));
	public static final FormedBlockShape WEDGE_INSIDE = new FormedBlockShape("wedge-i", m -> StairLike.create(m, Wedge.INSTANCE, StairLike.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH));
	public static final FormedBlockShape WEDGE_OUTSIDE = new FormedBlockShape("wedge-o", m -> StairLike.create(m, Wedge.INSTANCE, StairLike.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH));
	public static final FormedBlockShape WEDGE_CAP = new FormedBlockShape("wedge-c", m -> FormedNonCubicFacingBlock.create(m, WedgeCap.INSTANCE, Direction.DOWN));

	public static final FormedBlockShape SLAB = new FormedBlockShape("slab", m -> FormedNonCubicFacingBlock.create(m, Slab.INSTANCE, Direction.DOWN));

	public static final FormedBlockShape STAIR = new FormedBlockShape("stair", m -> StairLike.create(m, Stair.INSTANCE, StairLike.Shape.STRAIGHT, CubeRotation.DOWN_WEST));
	public static final FormedBlockShape STAIR_INSIDE = new FormedBlockShape("stair-i", m -> StairLike.create(m, Stair.INSTANCE, StairLike.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH));
	public static final FormedBlockShape STAIR_OUTSIDE = new FormedBlockShape("stair-o", m -> StairLike.create(m, Stair.INSTANCE, StairLike.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH));

	public static final FormedBlockShape SQUARE_COLUMN_GROOVED = new FormedBlockShape("sqcol-gr", SquareColumnGrooved::create);
	public static final FormedBlockShape SQUARE_COLUMN_CAPPED = new FormedBlockShape("sqcol-c", SquareColumnCapped::create);
	public static final FormedBlockShape ROUND_COLUMN = new FormedBlockShape("rcol", m -> RoundColumn.create(m));
	public static final FormedBlockShape ROUND_COLUMN_SQUARE_CAP = new FormedBlockShape("rcol-sqc", RoundColumnSquareCap::create);
	public static final FormedBlockShape ROUND_COLUMN_ROUND_CAP = new FormedBlockShape("rcol-rc", RoundColumnRoundCap::create);
	public static final FormedBlockShape ROUND_COLUMN_CUT = new FormedBlockShape("rcol-cut", RoundColumnCut::create);
}
