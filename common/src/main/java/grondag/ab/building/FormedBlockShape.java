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

import java.util.function.Consumer;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.world.level.block.Block;

import grondag.xm.api.connect.species.SpeciesProperty;
import grondag.xm.api.modelstate.primitive.PrimitiveStateMutator;
import grondag.xm.api.primitive.base.AbstractWedge;
import grondag.xm.api.primitive.simple.Cube;
import grondag.xm.api.primitive.simple.Stair;
import grondag.xm.api.primitive.simple.Wedge;
import grondag.xm.orientation.api.CubeRotation;

public abstract class FormedBlockShape {
	public final String code;

	private FormedBlockShape (
			String code
	) {
		this.code = code;
		ALL.add(this);
	}

	public abstract Block createBlock(BuildingMaterial material);

	private static final ObjectArrayList<FormedBlockShape> ALL = new ObjectArrayList<>();

	public static void forEach(Consumer<FormedBlockShape> consumer) {
		ALL.forEach(consumer);
	}

	private static final PrimitiveStateMutator SIMPLE_SPECIES_MUTATOR = PrimitiveStateMutator.builder()
			.withJoin(SpeciesProperty.matchBlockAndSpecies())
			.withUpdate(SpeciesProperty.SPECIES_MODIFIER)
			.build();

	public static final FormedBlockShape CUBE = new FormedBlockShape("cube-u") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			final var defaultState = Cube.INSTANCE.newState().paintAll(material.paint()).releaseToImmutable();
			return new FormedSpeciesBlock(material.settings(), Building::formedBlockEntity, defaultState, SIMPLE_SPECIES_MUTATOR);
		}
	};

	public static final FormedBlockShape WEDGE = new FormedBlockShape("wedge-s") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			final var defaultState = Wedge.INSTANCE.newState().orientationIndex(CubeRotation.DOWN_WEST.ordinal()).paintAll(material.paint()).releaseToImmutable();
			return new FormedStairLike(material.settings(), Building::formedBlockEntity, defaultState, FormedStairLike.MODELSTATE_FROM_BLOCKSTATE, FormedStairLike.Shape.STRAIGHT);
		}
	};

	public static final FormedBlockShape INSIDE_WEDGE = new FormedBlockShape("wedge-i") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			// Default orientation renders better for items in GUI
			final var defaultState = Wedge.INSTANCE.newState().orientationIndex(CubeRotation.DOWN_SOUTH.ordinal()).paintAll(material.paint());
			AbstractWedge.setCorner(true, defaultState);
			AbstractWedge.setInsideCorner(true, defaultState);
			return new FormedStairLike(material.settings(), Building::formedBlockEntity, defaultState.releaseToImmutable(), FormedStairLike.MODELSTATE_FROM_BLOCKSTATE, FormedStairLike.Shape.INSIDE_CORNER);
		}
	};

	public static final FormedBlockShape OUTSIDE_WEDGE = new FormedBlockShape("wedge-o") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			// Default orientation renders better for items in GUI
			final var defaultState = Wedge.INSTANCE.newState().orientationIndex(CubeRotation.DOWN_SOUTH.ordinal()).paintAll(material.paint());
			AbstractWedge.setCorner(true, defaultState);
			AbstractWedge.setInsideCorner(false, defaultState);
			return new FormedStairLike(material.settings(), Building::formedBlockEntity, defaultState.releaseToImmutable(), FormedStairLike.MODELSTATE_FROM_BLOCKSTATE, FormedStairLike.Shape.OUTSIDE_CORNER);
		}
	};

	public static final FormedBlockShape STAIR = new FormedBlockShape("stair-s") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			final var defaultState = Stair.INSTANCE.newState().orientationIndex(CubeRotation.DOWN_WEST.ordinal()).paintAll(material.paint()).releaseToImmutable();
			return new FormedStairLike(material.settings(), Building::formedBlockEntity, defaultState, FormedStairLike.MODELSTATE_FROM_BLOCKSTATE, FormedStairLike.Shape.STRAIGHT);
		}
	};

	public static final FormedBlockShape INSIDE_STAIR = new FormedBlockShape("stair-i") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			// Default orientation renders better for items in GUI
			final var defaultState = Stair.INSTANCE.newState().orientationIndex(CubeRotation.DOWN_SOUTH.ordinal()).paintAll(material.paint());
			AbstractWedge.setCorner(true, defaultState);
			AbstractWedge.setInsideCorner(true, defaultState);
			return new FormedStairLike(material.settings(), Building::formedBlockEntity, defaultState.releaseToImmutable(), FormedStairLike.MODELSTATE_FROM_BLOCKSTATE, FormedStairLike.Shape.INSIDE_CORNER);
		}
	};

	public static final FormedBlockShape OUTSIDE_STAIR = new FormedBlockShape("stair-o") {
		@Override
		public Block createBlock(BuildingMaterial material) {
			// Default orientation renders better for items in GUI
			final var defaultState = Stair.INSTANCE.newState().orientationIndex(CubeRotation.DOWN_SOUTH.ordinal()).paintAll(material.paint());
			AbstractWedge.setCorner(true, defaultState);
			AbstractWedge.setInsideCorner(false, defaultState);
			return new FormedStairLike(material.settings(), Building::formedBlockEntity, defaultState.releaseToImmutable(), FormedStairLike.MODELSTATE_FROM_BLOCKSTATE, FormedStairLike.Shape.OUTSIDE_CORNER);
		}
	};
}
