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
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;

import grondag.ab.building.block.PanelInset;
import grondag.ab.building.block.SquareColumnGrooved;
import grondag.ab.building.block.StairLike;
import grondag.ab.building.block.base.FormedNonCubicFacingBlock;
import grondag.ab.building.block.base.FormedNonCubicPillarBlock;
import grondag.ab.building.block.base.FormedSpeciesBlock;
import grondag.xm.api.connect.world.BlockConnectors;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.primitive.simple.CappedRoundColumn;
import grondag.xm.api.primitive.simple.CappedSquareInsetColumn;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.primitive.simple.CutRoundColumn;
import grondag.xm.api.primitive.simple.CylinderWithAxis;
import grondag.xm.api.primitive.simple.FlatPanel;
import grondag.xm.api.primitive.simple.InsetPanel;
import grondag.xm.api.primitive.simple.RoundCappedRoundColumn;
import grondag.xm.api.primitive.simple.Slab;
import grondag.xm.api.primitive.simple.Stair;
import grondag.xm.api.primitive.simple.Wedge;
import grondag.xm.api.primitive.simple.WedgeCap;
import grondag.xm.orientation.api.CubeRotation;

public abstract class FormedBlockShapes {
	private FormedBlockShapes() { }

	private static final ObjectArrayList<FormedBlockShape> ALL = new ObjectArrayList<>();

	public static void forEach(Consumer<FormedBlockShape> consumer) {
		ALL.forEach(consumer);
	}

	private static FormedBlockShape create(
			String code,
			Function<FormedBlockMaterial, PrimitiveState> defaultModelStateFunc,
			PrimitiveStateMutator stateFunc,
			Function<FormedBlockType, Block> factory,
			ShapeType shapeType
	) {
		final var result = new FormedBlockShape(code, defaultModelStateFunc, stateFunc, factory, shapeType);
		ALL.add(result);
		return result;
	}

	public static final FormedBlockShape CUBE = create("cube",
			material -> Cube.INSTANCE.newState().paintAll(material.paint()).releaseToImmutable(),
			FormedSpeciesBlock.SIMPLE_SPECIES_MUTATOR,
			FormedSpeciesBlock::new, ShapeType.STATIC_CUBE);

	public static final FormedBlockShape PANEL_INSET = create("pnl-ins",
			material -> InsetPanel.INSTANCE.newState()
				.paint(InsetPanel.SURFACE_OUTER, material.paint())
				.paint(InsetPanel.SURFACE_CUT, material.paintCut())
				.paint(InsetPanel.SURFACE_INNER, material.paintInner())
				.releaseToImmutable(),
			PrimitiveStateMutator.builder()
				.withJoin(BlockConnectors.SAME_BLOCK_OR_CONNECTABLE)
				.build(),
			PanelInset::new, ShapeType.STATIC_CUBE_WITH_CUTOUTS);

	public static final FormedBlockShape PANEL_FLAT = create("pnl-flt",
			material -> FlatPanel.INSTANCE.newState()
				.paint(FlatPanel.SURFACE_OUTER, material.paint())
				.paint(FlatPanel.SURFACE_INNER, material.paintInner())
				.releaseToImmutable(),
			FormedSpeciesBlock.SIMPLE_SPECIES_MUTATOR,
			FormedSpeciesBlock::new, ShapeType.STATIC_CUBE);

	public static final FormedBlockShape WEDGE = StairLike.createBlockShape("wedge", Wedge.INSTANCE, StairLike.Shape.STRAIGHT, CubeRotation.DOWN_WEST);
	public static final FormedBlockShape WEDGE_INSIDE = StairLike.createBlockShape("wedge-i", Wedge.INSTANCE, StairLike.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH);
	public static final FormedBlockShape WEDGE_OUTSIDE = StairLike.createBlockShape("wedge-o", Wedge.INSTANCE, StairLike.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH);

	public static final FormedBlockShape WEDGE_CAP = FormedNonCubicFacingBlock.createBlockShape("wedge-c", WedgeCap.INSTANCE, Direction.DOWN);
	public static final FormedBlockShape SLAB = FormedNonCubicFacingBlock.createBlockShape("slab", Slab.INSTANCE, Direction.DOWN);

	public static final FormedBlockShape STAIR = StairLike.createBlockShape("stair", Stair.INSTANCE, StairLike.Shape.STRAIGHT, CubeRotation.DOWN_WEST);
	public static final FormedBlockShape STAIR_INSIDE = StairLike.createBlockShape("stair-i", Stair.INSTANCE, StairLike.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH);
	public static final FormedBlockShape STAIR_OUTSIDE = StairLike.createBlockShape("stair-o", Stair.INSTANCE, StairLike.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH);

	public static final FormedBlockShape SQUARE_COLUMN_GROOVED = create("sqcol-gr",
			SquareColumnGrooved::createDefaultModelState,
			FormedNonCubicPillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			SquareColumnGrooved::new, ShapeType.STATIC_CUBE_WITH_CUTOUTS);

	public static final FormedBlockShape SQUARE_COLUMN_CAPPED = create("sqcol-c",
			material -> CappedSquareInsetColumn.INSTANCE.newState()
				.paint(CappedSquareInsetColumn.SURFACE_OUTER, material.paint())
				.paint(CappedSquareInsetColumn.SURFACE_ENDS, material.paint())
				.paint(CappedSquareInsetColumn.SURFACE_INNER, material.paintInner())
				.paint(CappedSquareInsetColumn.SURFACE_CUT, material.paintCut())
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			FormedNonCubicPillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			FormedNonCubicPillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC);

	public static final FormedBlockShape ROUND_COLUMN = create("rcol",
			material -> CylinderWithAxis.INSTANCE.newState()
				.orientationIndex(Axis.Y.ordinal())
				.paintAll(material.paint())
				.releaseToImmutable(),
			PrimitiveStateMutator.builder()
				.withUpdate(PrimitiveState.AXIS_FROM_BLOCKSTATE)
				.build(),
			FormedNonCubicPillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC);

	public static final FormedBlockShape ROUND_COLUMN_SQUARE_CAP = create("rcol-sqc",
			material -> CappedRoundColumn.INSTANCE.newState()
				.paintAll(material.paint())
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			FormedNonCubicPillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			FormedNonCubicPillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC);

	public static final FormedBlockShape ROUND_COLUMN_ROUND_CAP = create("rcol-rc",
			material -> RoundCappedRoundColumn.INSTANCE.newState()
				.paint(RoundCappedRoundColumn.SURFACE_OUTER, material.paint())
				.paint(RoundCappedRoundColumn.SURFACE_ENDS, material.paint())
				.paint(RoundCappedRoundColumn.SURFACE_INNER, material.paint())
				.paint(RoundCappedRoundColumn.SURFACE_CUT, material.paintCut())
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			FormedNonCubicPillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			FormedNonCubicPillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC);

	public static final FormedBlockShape ROUND_COLUMN_CUT = create("rcol-cut",
			material -> CutRoundColumn.INSTANCE.newState()
				.paint(CutRoundColumn.SURFACE_OUTER, material.paint())
				.paint(CutRoundColumn.SURFACE_ENDS, material.paint())
				.paint(CutRoundColumn.SURFACE_INNER, material.paintInner())
				.paint(CutRoundColumn.SURFACE_CUT, material.paintCut())
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			FormedNonCubicPillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			FormedNonCubicPillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC);
}
