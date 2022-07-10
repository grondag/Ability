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

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import grondag.ab.building.block.base.FormedNonCubicSpeciesBlock;
import grondag.ab.building.block.init.FormedBlockMaterial;
import grondag.xm.api.collision.CollisionShapes;
import grondag.xm.api.connect.world.BlockConnectors;
import grondag.xm.api.connect.world.FenceHelper;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.primitive.simple.InsetPanel;

public class PanelInset extends FormedNonCubicSpeciesBlock {
	public PanelInset(Properties settings, PrimitiveState defaultModelState, PrimitiveStateMutator stateFunc) {
		super(settings, defaultModelState, stateFunc);
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState blockState, BlockGetter blockView, BlockPos pos, CollisionContext entityContext) {
		return CollisionShapes.CUBE_WITH_CUTOUTS;
	}

	public static PanelInset create(FormedBlockMaterial material) {
		final var defaultState = InsetPanel.INSTANCE.newState()
				.paint(InsetPanel.SURFACE_OUTER, material.paint())
				.paint(InsetPanel.SURFACE_CUT, material.paintCut())
				.paint(InsetPanel.SURFACE_INNER, material.paintInner());

		final PrimitiveStateMutator stateFunc = PrimitiveStateMutator.builder()
			.withJoin(BlockConnectors.SAME_BLOCK_OR_CONNECTABLE)
			.build();

		final var result = new PanelInset(material.settings(), defaultState.releaseToImmutable(), stateFunc);

		FenceHelper.add(result);

		return result;
	}
}
