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

package grondag.ab.building;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;

import grondag.ab.Ability;
import grondag.xm.api.connect.species.Species;
import grondag.xm.api.connect.species.SpeciesFunction;
import grondag.xm.api.connect.species.SpeciesMode;
import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;

public class FormedSpeciesBlock extends FormedBlock {
	private final SpeciesFunction speciesFunc = SpeciesProperty.speciesForBlock(this);

	protected FormedSpeciesBlock(Properties settings, BlockEntitySupplier<FormedBlockEntity> beFactory, PrimitiveState defaultModelState, PrimitiveStateMutator stateFunc) {
		super(settings, beFactory, defaultModelState, stateFunc);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(SpeciesProperty.SPECIES);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		final var mode = Ability.forceKey.isPressed(context.getPlayer()) ? SpeciesMode.COUNTER_MOST : SpeciesMode.MATCH_MOST;
		final int species = Species.speciesForPlacement(context, mode, speciesFunc);
		return defaultBlockState().setValue(SpeciesProperty.SPECIES, species);
	}
}
