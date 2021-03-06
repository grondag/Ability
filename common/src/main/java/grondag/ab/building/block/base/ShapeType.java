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

import java.util.function.Function;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public enum ShapeType {
	CUBE(p -> p, true),
	OCCLUDING_CUBE_WITH_CUTOUTS(p -> p, true),
	DYNAMIC_CUBE_WITH_CUTOUTS(p -> p.dynamicShape(), false),
	STATIC_NON_CUBIC(p -> p, false),
	DYNAMIC_NON_CUBIC(p -> p.dynamicShape(), false);

	public final Function<Properties, Properties> setup;
	public final boolean isFullOccluder;

	ShapeType(Function<Properties, Properties> setup, boolean isFullOccluder) {
		this.setup = setup;
		this.isFullOccluder = isFullOccluder;
	}
}