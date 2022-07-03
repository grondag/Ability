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

import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class FormedBlockMaterials {
	private FormedBlockMaterials() { }

	public static final Material DURACRETE = new Material.Builder(MaterialColor.STONE).build();
	public static final Material DURAGLASS = new Material.Builder(MaterialColor.NONE).build();
	public static final Material DURAWOOD = new Material.Builder(MaterialColor.WOOD).build();
	public static final Material DURASTEEL = new Material.Builder(MaterialColor.METAL).build();

	public static final Material HYPERCRETE = new Material.Builder(MaterialColor.STONE).notPushable().build();
	public static final Material HYPERGLASS = new Material.Builder(MaterialColor.NONE).notPushable().build();
	public static final Material HYPERWOOD = new Material.Builder(MaterialColor.WOOD).notPushable().build();
	public static final Material HYPERSTEEL = new Material.Builder(MaterialColor.METAL).notPushable().build();
}
