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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class FormedBlockSettings {
	private FormedBlockSettings() { }

	private static final float DURA_HARDNESS = 50.0F;
	private static final float DURA_RESISTANCE = 1200.0F;

	private static final float DURASTEEL_MULTIPLIER = 1.2F;
	private static final float DURAWOOD_MULTIPLIER = 0.8F;
	private static final float DURAGLASS_MULTIPLIER = 0.8F;

	private static final float HYPER_SLIP = 0.989F;

	public static Properties duraSteel() {
		return Block.Properties.of(FormedBlockMaterials.DURASTEEL)
				//.breakByTool(FabricToolTags.PICKAXES, 3)
				.requiresCorrectToolForDrops()
				.sound(SoundType.METAL)
				.strength(DURA_HARDNESS, DURA_RESISTANCE);
	}

	public static Properties duraCrete() {
		return Block.Properties.of(FormedBlockMaterials.DURACRETE)
				.requiresCorrectToolForDrops()
				.sound(SoundType.STONE)
				.strength(DURA_HARDNESS * DURASTEEL_MULTIPLIER, DURA_RESISTANCE * DURASTEEL_MULTIPLIER);
	}

	public static Properties duraGlass() {
		return Block.Properties.of(FormedBlockMaterials.DURAGLASS)
				.requiresCorrectToolForDrops()
				.sound(SoundType.GLASS)
				.noOcclusion()
				.strength(DURA_HARDNESS * DURAGLASS_MULTIPLIER, DURA_RESISTANCE * DURAGLASS_MULTIPLIER);
	}

	public static Properties duraWood() {
		return Block.Properties.of(FormedBlockMaterials.DURAWOOD)
				.requiresCorrectToolForDrops()
				.sound(SoundType.WOOD)
				.strength(DURA_HARDNESS * DURAWOOD_MULTIPLIER, DURA_RESISTANCE * DURAWOOD_MULTIPLIER);
	}

	public static Properties hyperSteel() {
		return Block.Properties.of(FormedBlockMaterials.HYPERSTEEL)
				.requiresCorrectToolForDrops()
				.sound(SoundType.METAL)
				.isValidSpawn((s, w, p, t) -> false)
				.friction(HYPER_SLIP)
				.strength(-1.0F, 3600000.0F);
	}

	public static Properties hyperCrete() {
		return Block.Properties.of(FormedBlockMaterials.HYPERCRETE)
				.requiresCorrectToolForDrops()
				.sound(SoundType.STONE)
				.isValidSpawn((s, w, p, t) -> false)
				.friction(HYPER_SLIP)
				.strength(-1.0F, 3600000.0F);
	}

	public static Properties hyperGlass() {
		return Block.Properties.of(FormedBlockMaterials.HYPERGLASS)
				.requiresCorrectToolForDrops()
				.sound(SoundType.GLASS)
				.noOcclusion()
				.isValidSpawn((s, w, p, t) -> false)
				.friction(HYPER_SLIP)
				.strength(-1.0F, 3600000.0F);
	}

	public static Properties hyperWood() {
		return Block.Properties.of(FormedBlockMaterials.HYPERWOOD)
				.requiresCorrectToolForDrops()
				.sound(SoundType.WOOD)
				.isValidSpawn((s, w, p, t) -> false)
				.friction(HYPER_SLIP)
				.strength(-1.0F, 3600000.0F);
	}
}
