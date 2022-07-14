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

package grondag.ab.building.block.init;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import grondag.ab.building.block.base.FormedBlockMaterial;
import grondag.ab.ux.client.color.BlockColors;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.texture.XmTextures;
import grondag.xm.api.texture.core.CoreTextures;
import grondag.xm.api.texture.unstable.UnstableTextures;

public class FormedBlockMaterials {
	public static final FormedBlockMaterial VIRTUAL = new FormedBlockMaterial(
		"virtual", SoundType.SNOW, false, false, false, 0.0f, 0.0f,
		new Material.Builder(MaterialColor.NONE).replaceable().noCollider().nonSolid().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_SUBTLE).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final FormedBlockMaterial DURAFOAM = new FormedBlockMaterial(
		"dfoam", SoundType.SNOW, false, false, false, 10.0f, 200.0f,
		new Material.Builder(MaterialColor.CLAY).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_SUBTLE).textureColor(0, 0XFFE0E0D0).find()
	);

	public static final FormedBlockMaterial DURACRETE = new FormedBlockMaterial(
		"dcrete", SoundType.STONE, false, false, false, 20.0f, 1200.0f,
		new Material.Builder(MaterialColor.STONE).build(),
		XmPaint.finder().texture(0, CoreTextures.BIGTEX_SANDSTONE).textureColor(0, 0XFFA0A0A0).find()
	);

	public static final FormedBlockMaterial DURAGLASS = new FormedBlockMaterial(
		"dglass", SoundType.GLASS, false, false, true, 18.0f, 1000.0f,
		new Material.Builder(MaterialColor.NONE).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_SUBTLE).textureColor(0, 0X80E0E0F0).find()
	);

	public static final FormedBlockMaterial DURAWOOD = new FormedBlockMaterial(
		"dwood", SoundType.WOOD, false, false, false, 10.0f, 600.0f,
		new Material.Builder(MaterialColor.WOOD).build(),
		XmPaint.finder().texture(0, UnstableTextures.BIGTEX_WOOD).textureColor(0, 0XFFE0E080).find()
	);

	public static final FormedBlockMaterial DURASTEEL = new FormedBlockMaterial(
		"dsteel", SoundType.METAL, false, false, false, 25.0f, 1400.0f,
		new Material.Builder(MaterialColor.METAL).build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_SUBTLE).textureColor(0, 0XFF8080A0).find()
	);

	public static final FormedBlockMaterial HYPERCRETE = new FormedBlockMaterial(
		"hcrete", SoundType.STONE, false, false, false, -1.0F, 3600000.0F,
		new Material.Builder(MaterialColor.STONE).notPushable().build(),
		XmPaint.finder().texture(0, CoreTextures.BIGTEX_SANDSTONE).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final FormedBlockMaterial HYPERGLASS = new FormedBlockMaterial(
		"hglass", SoundType.GLASS, false, false, true, -1.0F, 3600000.0F,
		new Material.Builder(MaterialColor.NONE).notPushable().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_SUBTLE).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final FormedBlockMaterial HYPERSTEEL = new FormedBlockMaterial(
		"hsteel", SoundType.METAL, false, false, false, -1.0F, 3600000.0F,
		new Material.Builder(MaterialColor.METAL).notPushable().build(),
		XmPaint.finder().texture(0, XmTextures.TILE_NOISE_SUBTLE).textureColor(0, BlockColors.DEFAULT_WHITE_RGB).find()
	);

	public static final ImmutableList<FormedBlockMaterial> CONVENTIONAL = ImmutableList.of(DURAFOAM, DURAGLASS, DURACRETE, DURAWOOD, DURASTEEL);
}
