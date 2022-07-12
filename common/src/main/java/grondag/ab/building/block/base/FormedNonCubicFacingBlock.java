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

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

import grondag.ab.building.block.init.FormedBlockShape;
import grondag.ab.building.block.init.FormedBlockType;
import grondag.ab.building.block.init.ShapeType;
import grondag.xm.api.block.XmProperties;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.primitive.SimplePrimitive;

public class FormedNonCubicFacingBlock extends FormedNonCubicBlock {
	public FormedNonCubicFacingBlock(FormedBlockType blockType, Direction defaultFace) {
		super(blockType);
		registerDefaultState(defaultBlockState().setValue(DirectionalBlock.FACING, defaultFace));
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(DirectionalBlock.FACING);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return super.getStateForPlacement(ctx).setValue(DirectionalBlock.FACING, ctx.getClickedFace().getOpposite());
	}

	public static FormedBlockShape createBlockShape(String name, SimplePrimitive primitive, Direction defaultFace) {
		return new FormedBlockShape("name",
			material -> primitive.newState().paintAll(material.paint()).orientationIndex(defaultFace.ordinal()),
			PrimitiveStateMutator.builder().withUpdate(XmProperties.FACE_MODIFIER).build(),
			bt -> new FormedNonCubicFacingBlock(bt, defaultFace), ShapeType.DYNAMIC_NON_CUBIC);
	}
}
