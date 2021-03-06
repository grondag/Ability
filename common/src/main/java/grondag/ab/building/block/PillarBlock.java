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

package grondag.ab.building.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import grondag.ab.building.block.base.FormedBlockType;
import grondag.xm.api.connect.world.BlockConnectors;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public class PillarBlock extends BasicBlock {
	public PillarBlock(FormedBlockType blockType) {
		super(blockType);
		registerDefaultState(defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.Y));
	}

	@Deprecated
	@Override
	public BlockState rotate(BlockState blockState, Rotation rotation) {
		return RotatedPillarBlock.rotatePillar(blockState, rotation);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(RotatedPillarBlock.AXIS);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		return super.getStateForPlacement(blockPlaceContext)
				.setValue(RotatedPillarBlock.AXIS, blockPlaceContext.getClickedFace().getAxis());
	}

	public static final PrimitiveStateMutator AXIS_JOIN_COLUMN_MUTATOR = PrimitiveStateMutator.builder()
			.withJoin(BlockConnectors.AXIS_JOIN_SAME_OR_CONNECTABLE)
			.withUpdate(PrimitiveState.AXIS_FROM_BLOCKSTATE)
			.build();
}
