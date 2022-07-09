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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import grondag.ab.ux.client.color.BlockColors;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.texture.XmTextures;

public record BuildingMaterial (
		String code, SoundType sound, boolean hyper, boolean needsTool, boolean clear, float hardness, float resistance,
		Material material,
		XmPaint paint
) {
	public static final BuildingMaterial VIRTUAL = new BuildingMaterial(
		"vn", SoundType.SNOW, false, false, false, 0.0f, 0.0f,
		new Material.Builder(MaterialColor.NONE).replaceable().noCollider().nonSolid().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial DURAFOAM = new BuildingMaterial(
		"df", SoundType.SNOW, false, false, false, 10.0f, 200.0f,
		new Material.Builder(MaterialColor.CLAY).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial DURACRETE = new BuildingMaterial(
		"dc", SoundType.STONE, false, false, false, 20.0f, 1200.0f,
		new Material.Builder(MaterialColor.STONE).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial DURAGLASS = new BuildingMaterial(
		"dg", SoundType.GLASS, false, false, true, 18.0f, 1000.0f,
		new Material.Builder(MaterialColor.NONE).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial DURAWOOD = new BuildingMaterial(
		"dw", SoundType.WOOD, false, false, false, 10.0f, 600.0f,
		new Material.Builder(MaterialColor.WOOD).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial DURASTEEL = new BuildingMaterial(
		"ds", SoundType.METAL, false, false, false, 25.0f, 1400.0f,
		new Material.Builder(MaterialColor.METAL).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial HYPERCRETE = new BuildingMaterial(
		"hc", SoundType.STONE, false, false, false, -1.0F, 3600000.0F,
		new Material.Builder(MaterialColor.STONE).notPushable().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial HYPERGLASS = new BuildingMaterial(
		"hg", SoundType.GLASS, false, false, true, -1.0F, 3600000.0F,
		new Material.Builder(MaterialColor.NONE).notPushable().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final BuildingMaterial HYPERSTEEL = new BuildingMaterial(
		"hs", SoundType.METAL, false, false, false, -1.0F, 3600000.0F,
		new Material.Builder(MaterialColor.METAL).notPushable().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_LIGHT).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	private static final float HYPER_SLIP = 0.989F;

	public Block.Properties settings() {
		final var result = Block.Properties.of(material())
				.sound(sound)
				.strength(hardness, resistance);

		if (needsTool) {
			result.requiresCorrectToolForDrops();
		}

		if (clear) {
			result.noOcclusion();
		}

		if (this == VIRTUAL) {
			result.noCollission();
		}

		if (hyper) {
			result.isValidSpawn((s, w, p, t) -> false);
			result.friction(HYPER_SLIP);
		}

		return result;
	}
}
