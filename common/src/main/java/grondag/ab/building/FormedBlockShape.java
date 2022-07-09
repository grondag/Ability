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

import net.minecraft.world.level.block.Block;

import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.primitive.simple.Cube;

public abstract class FormedBlockShape {
	public final String code;

	private FormedBlockShape (
			String code
	) {
		this.code = code;
	}

	public abstract Block createBlock(BuildingMaterial material);

	private static final PrimitiveStateMutator SIMPLE_SPECIES_MUTATOR = PrimitiveStateMutator.builder()
			.withJoin(SpeciesProperty.matchBlockAndSpecies())
			.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
			.build();

	public static final FormedBlockShape CUBE = new FormedBlockShape("c") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			final var defaultState = Cube.INSTANCE.newState().paintAll(material.paint()).releaseToImmutable();
			return new FormedSpeciesBlock(material.settings(), Building::formedBlockEntity, defaultState, SIMPLE_SPECIES_MUTATOR);
		}
	};
}
