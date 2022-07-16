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

import java.util.function.Function;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.Block;

import grondag.ab.building.block.BasicBlock;
import grondag.ab.building.block.FacingBlock;
import grondag.ab.building.block.PillarBlock;
import grondag.ab.building.block.StairLikeBlock;
import grondag.ab.building.block.base.FormedBlockMaterial;
import grondag.ab.building.block.base.FormedBlockShape;
import grondag.ab.building.block.base.FormedBlockType;
import grondag.ab.building.block.base.ShapeType;
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
import grondag.xm.api.primitive.simple.SquareColumn;
import grondag.xm.api.primitive.simple.Stair;
import grondag.xm.api.primitive.simple.Wedge;
import grondag.xm.api.primitive.simple.WedgeCap;
import grondag.xm.orientation.api.CubeRotation;

public abstract class FormedBlockShapes {
	private FormedBlockShapes() { }

	static void initialize() {
		// NOOP currently - only here to force initialize of static members
	}

	static FormedBlockShape create(
			String code,
			Function<FormedBlockMaterial, PrimitiveState> defaultModelStateFunc,
			PrimitiveStateMutator stateFunc,
			Function<FormedBlockType, Block> factory,
			ShapeType shapeType,
			boolean useSpecies
	) {
		return new FormedBlockShape(code, defaultModelStateFunc, stateFunc, factory, shapeType, useSpecies);
	}

	public static final FormedBlockShape CUBE = create("cube",
			material -> Cube.INSTANCE.newState().apply(material.createPaintState()::applyToState).releaseToImmutable(),
			BasicBlock.SIMPLE_SPECIES_MUTATOR,
			BasicBlock::new, ShapeType.CUBE, true);

	public static final FormedBlockShape PANEL_INSET = create("pnl-ins",
			material -> InsetPanel.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.releaseToImmutable(),
			PrimitiveStateMutator.builder()
				.withJoin(BlockConnectors.SAME_BLOCK_OR_CONNECTABLE)
				.build(),
				PillarBlock::new, ShapeType.OCCLUDING_CUBE_WITH_CUTOUTS, true);

	public static final FormedBlockShape PANEL_FLAT = create("pnl-flt",
			material -> FlatPanel.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.releaseToImmutable(),
			BasicBlock.SIMPLE_SPECIES_MUTATOR,
			BasicBlock::new, ShapeType.CUBE, true);

	public static final FormedBlockShape WEDGE = StairLikeBlock.createBlockShape("wedge", Wedge.INSTANCE, StairLikeBlock.Shape.STRAIGHT, CubeRotation.DOWN_WEST);
	public static final FormedBlockShape WEDGE_INSIDE = StairLikeBlock.createBlockShape("wedge-i", Wedge.INSTANCE, StairLikeBlock.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH);
	public static final FormedBlockShape WEDGE_OUTSIDE = StairLikeBlock.createBlockShape("wedge-o", Wedge.INSTANCE, StairLikeBlock.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH);

	public static final FormedBlockShape WEDGE_CAP = FacingBlock.createBlockShape("wedge-c", WedgeCap.INSTANCE, Direction.DOWN);
	public static final FormedBlockShape SLAB = FacingBlock.createBlockShape("slab", Slab.INSTANCE, Direction.DOWN);

	public static final FormedBlockShape STAIR = StairLikeBlock.createBlockShape("stair", Stair.INSTANCE, StairLikeBlock.Shape.STRAIGHT, CubeRotation.DOWN_WEST);
	public static final FormedBlockShape STAIR_INSIDE = StairLikeBlock.createBlockShape("stair-i", Stair.INSTANCE, StairLikeBlock.Shape.INSIDE_CORNER, CubeRotation.DOWN_SOUTH);
	public static final FormedBlockShape STAIR_OUTSIDE = StairLikeBlock.createBlockShape("stair-o", Stair.INSTANCE, StairLikeBlock.Shape.OUTSIDE_CORNER, CubeRotation.DOWN_SOUTH);

	public static final FormedBlockShape SQUARE_COLUMN_GROOVED = create("sqcol-gr",
			material -> SquareColumn.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.orientationIndex(Axis.Y.ordinal())
				.apply(s -> SquareColumn.setCutCount(4, s))
				.apply(s -> SquareColumn.setCutsOnEdge(true, s))
				.releaseToImmutable(),
			PillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			PillarBlock::new, ShapeType.DYNAMIC_CUBE_WITH_CUTOUTS, false);

	public static final FormedBlockShape SQUARE_COLUMN_CAPPED = create("sqcol-c",
			material -> CappedSquareInsetColumn.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			PillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			PillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC, false);

	public static final FormedBlockShape ROUND_COLUMN = create("rcol",
			material -> CylinderWithAxis.INSTANCE.newState()
				.orientationIndex(Axis.Y.ordinal())
				.apply(material.createPaintState()::applyToState)
				.releaseToImmutable(),
			PrimitiveStateMutator.builder()
				.withUpdate(PrimitiveState.AXIS_FROM_BLOCKSTATE)
				.build(),
			PillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC, false);

	public static final FormedBlockShape ROUND_COLUMN_SQUARE_CAP = create("rcol-sqc",
			material -> CappedRoundColumn.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			PillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			PillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC, false);

	public static final FormedBlockShape ROUND_COLUMN_ROUND_CAP = create("rcol-rc",
			material -> RoundCappedRoundColumn.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			PillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			PillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC, false);

	public static final FormedBlockShape ROUND_COLUMN_CUT = create("rcol-cut",
			material -> CutRoundColumn.INSTANCE.newState()
				.apply(material.createPaintState()::applyToState)
				.orientationIndex(Axis.Y.ordinal())
				.releaseToImmutable(),
			PillarBlock.AXIS_JOIN_COLUMN_MUTATOR,
			PillarBlock::new, ShapeType.DYNAMIC_NON_CUBIC, false);
}
