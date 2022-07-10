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

import grondag.ab.building.block.base.FormedSpeciesBlock;
import grondag.ab.building.block.init.FormedBlockMaterial;
import grondag.xm.api.modelstate.primitive.PrimitiveState;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.primitive.simple.FlatPanel;

public class PanelFlat extends FormedSpeciesBlock {
	public PanelFlat(Properties settings, PrimitiveState defaultModelState, PrimitiveStateMutator stateFunc) {
		super(settings, defaultModelState, stateFunc);
	}

	public static PanelFlat create(FormedBlockMaterial material) {
		final var defaultState = FlatPanel.INSTANCE.newState()
				.paint(FlatPanel.SURFACE_OUTER, material.paint())
				.paint(FlatPanel.SURFACE_INNER, material.paintInner())
				.releaseToImmutable();

		final var block = new PanelFlat(material.settings(), defaultState, SIMPLE_SPECIES_MUTATOR);

		return block;
	}
}
