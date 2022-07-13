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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

import grondag.ab.building.block.init.FormedBlockMaterials;
import grondag.xm.api.paint.XmPaint;
import grondag.xm.api.util.ColorUtil;

public record FormedBlockMaterial (
		String code, SoundType sound, boolean hyper, boolean needsTool, boolean clear, float hardness, float resistance,
		Material material,
		XmPaint paint
) {
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

		if (this == FormedBlockMaterials.VIRTUAL) {
			result.noCollission();
		}

		if (hyper) {
			result.isValidSpawn((s, w, p, t) -> false);
			result.friction(HYPER_SLIP);
		}

		return result;
	}

	public XmPaint paintInner() {
		return XmPaint.finder().copy(paint).textureColor(0, ColorUtil.multiplyRGB(paint.textureColor(0), 0.85f)).find();
	}

	public XmPaint paintCut() {
		return XmPaint.finder().copy(paint).textureColor(0, ColorUtil.multiplyRGB(paint.textureColor(0), 0.92f)).find();
	}

	private static final float HYPER_SLIP = 0.989F;
}
